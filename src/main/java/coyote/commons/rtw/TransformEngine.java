/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw;


import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.context.ContextListener;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.template.SymbolTable;

import java.io.File;
import java.io.IOException;


/**
 * This is the interface all transform engines implement.
 */
public interface TransformEngine extends RunnableComponent {

    void addListener(ContextListener listener);


    TransformContext getContext();

    void setContext(TransformContext context);

    FrameMapper getMapper();

    void setMapper(FrameMapper mapper);

    FrameReader getReader();

    void setReader(FrameReader reader);


    void addWriter(FrameWriter writer);


    /**
     * Add the given aggregator to the engine.
     *
     * @param aggregator the aggregator to add
     */
    void addAggregator(FrameAggregator aggregator);


    /**
     * @return the name of the transform engine
     */
    String getName();


    /**
     * @param name the transform engine name to set
     */
    void setName(String name);


    /**
     * @return the symbol table the engine uses for template lookups
     */
    SymbolTable getSymbolTable();


    /**
     * Add the task to an ordered list of tasks to be run before the transform is
     * run.
     *
     * <p>Tasks will be executed in the order they were added to the engine.</p>
     *
     * @param task the task to add
     * @return the sequence in which it will be executed
     */
    int addPreProcessTask(TransformTask task);


    /**
     * Add the task to an ordered list of tasks to be run after the transform is
     * run.
     *
     * <p>Tasks will be executed in the order they were added to the engine.</p>
     *
     * @param task the task to add
     * @return the sequence in which it will be executed
     */
    int addPostProcessTask(TransformTask task);


    void addValidator(FrameValidator validator);


    void addTransformer(FrameTransform transformer);


    /**
     * Called when the JVM terminates.
     *
     * <p>This is designed to be called by the Runtime.shutdownHook to terminate
     * any long-running processes and after the engine terminates normally. This
     * is not part of the transform engine run loop, but part of the Loader life
     * cycle management loop.</p>
     *
     * <p>This should always be called when the runtime terminates. The only
     * time this might not be called is when the process terminates abnormally.
     * In which case it is possible that everything in the runtime will be
     * terminated abruptly as well.</p>
     */
    void shutdown();


    /**
     * Signals the component to stop processing; called when the engine is being
     * requested to shutdown by some component.
     *
     * <p>Shut this component down using the given DataFrame as a set of
     * parameters
     *
     * @param params Shutdown arguments, can be null.
     */
    void shutdown(DataFrame params);


    /**
     * The Job Directory is a location on the local file system in which we can
     * write and read files this engine and its components can use.
     *
     * <p>This is a place where data can be persisted between multiple runs of
     * the same job. The output of a job can be read in later. For example, the
     * job can store its last run time in this directory and when it runs again,
     * it can query that time to determine how long it has been since it last
     * ran.</p>
     *
     * @return the directory in which we can read and write our own files.
     */
    File getJobDirectory();


    /**
     * This sets a common sharable location on the file system where the engine
     * can find data shared by components in this engine.
     *
     * <p>It is in this directory where the engine's component data can be
     * persisted between runs.
     *
     * @param dir the directory to be used as a sharable space on the file system
     */
    void setJobDirectory(File dir);


    /**
     * The Work Directory is a common location of many jobs in the system.
     *
     * <p>This is a place where data can be persisted between jobs. For example,
     * a job may generate a list of users it has imported from another system as
     * a file and multiple other jobs can use that file as input to their
     * operations. These other jobs do not know how that file got generated, only
     * that it has the data it needs. This provides a level of abstraction
     * between jobs.</p>
     *
     * <p>Also, this directory can be used to store the state of the jobs and
     * other jobs can determine if they should run based on the presence of data
     * in that directory.</p>
     *
     * @return the directory in which we can read and write files to share with other engines and jobs.
     */
    File getWorkDirectory();


    /**
     * This sets a common sharable location on the file system where the engine
     * can find data shared by other jobs.
     *
     * <p>It is in this directory where the engine's data can be persisted
     * between runs when sharing data with other jobs.
     *
     * @param dir the directory to be used as a sharable space on the file system
     */
    void setWorkDirectory(File dir);

    /**
     * Access the LogManager for this engine.
     *
     * @return The log manager for this engine or null if no manager exists.
     */
    LogManager getLogManager();

    /**
     * This allows a component to be set which will initialize logging in the
     * engine in coordination with its life cycle and for loggers to use value in
     * the engines transform context.
     *
     * @param logmgr The logging manager to use for this engine.
     */
    void setLogManager(LogManager logmgr);

    /**
     * Adds a frame filter to the engine.
     *
     * <p>Frame Filters (or just "filters") are designed to remove data from the
     * stream which is not to be processed. For example, some readers have the
     * ability to read many different frame formats and the engine is intended to
     * handle only one.</p>
     *
     * <p>This is not to be confused with Validators which generate error events
     * when data does not match expectations. Filters prevent data from entering
     * the processing stream and if a frame is rejected, it never is validate or
     * otherwise processed by the transform engine.</p>
     *
     * <p>The engine will apply filters in the order they are added. This allows
     * for creating a few {@code accept} filters and one catch-all {@code reject}
     * filter to filter-out any unexpected data.</p>
     *
     * @param filter the filter to add
     * @return the sequence in which it will be executed
     */
    int addFilter(FrameFilter filter);


    /**
     * Access the globally unique identifier for this instance.
     *
     * <p>The name of an engine is logically unique but a named job will run
     * many times and only the instance identifier is unique across all
     * instances of that named job. For example, The "IncidentImport" job may
     * run 24 times each day. The only way to uniquely identify one of those
     * jobs is by using its instance identifier.
     *
     * @return the unique identifier for this instance.
     */
    String getInstanceId();


    /**
     * Initialize the transform context
     *
     * @return the initialized context
     */
    TransformContext contextInit();


    /**
     * @param reader the reader to be used for preloading data into some components.
     */
    void setPreloader(FrameReader reader);


    void close() throws IOException;
}