/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import coyote.BootStrap;
import coyote.commons.ExceptionUtil;
import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.log.Log;
import coyote.commons.rtw.*;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.template.Template;



/**
 * This task runs a data transfer job using the current context.
 * 
 * <p>Using this task, it is possible to run several jobs as one, each with a 
 * set of conditions determining if it should run. This give allows for more 
 * complex processing scenarios.
 * 
 * <p>This task can be configured thusly:<pre>
 * "RunJob" : { "file": "somejob.json", "name": "jobname" }</pre>
 * 
 * <p>The {@code file} parameter specifies the data transfer job configuration 
 * to run.
 * 
 * <p>The {@code name} parameter specifies the name to use for the job. This
 * allows for the publication of data in different locations than those 
 * specified in the configuration file or the default values.
 */
public class RunJob extends AbstractTransformTask implements TransformTask {




  /**
   * Confirm the configuration URI.
   *
   * @param cfgLoc the location of the configuration file.
   * @return the URI of the configuration file, or null if it could not be found.
   * @throws TaskException if there was a problem confirming the configuration location.
   */
  private URI confirmConfigurationLocation(final String cfgLoc) throws TaskException {
    URI cfgUri = null;
    final StringBuffer errMsg = new StringBuffer(String.format("Confirming configuration location: %s", cfgLoc) + StringUtil.CRLF);

    if (StringUtil.isNotBlank(cfgLoc)) {

      // create a URI out of it
      try {
        cfgUri = new URI(cfgLoc);
      } catch (final URISyntaxException e) {
        // This can happen when the location is a filename
      }

      // No URI implies a file
      if ((cfgUri == null) || StringUtil.isBlank(cfgUri.getScheme())) {

        final File localfile = new File(cfgLoc);

        if (!localfile.isAbsolute()) {
          cfgUri = checkCurrentDirectory(cfgLoc, errMsg);
          if (cfgUri == null) {
            cfgUri = checkWorkDirectory(cfgLoc, errMsg);
          }
          if (cfgUri == null) {
            cfgUri = checkAppCfgDirectory(cfgLoc, errMsg);
          }
        }

        if (cfgUri == null) {
          errMsg.append(String.format("Configuration file not found: %s", cfgLoc) + StringUtil.CRLF);
          if (haltOnError) {
            throw new TaskException(errMsg.toString());
          } else {
            Log.error(errMsg.toString());
          }
        } else {
          final File test = UriUtil.getFile(cfgUri);
          if (!test.exists() || !test.canRead()) {
            errMsg.append(String.format("Configuration file not readable: %s", test.getAbsolutePath()) + StringUtil.CRLF);
            if (haltOnError) {
              throw new TaskException(errMsg.toString());
            } else {
              Log.error(errMsg.toString());
            }
          } else {
            Log.debug(String.format("Reading configuration from file: %s", test.getAbsolutePath()));
          }
        }
      } else {
        Log.info("Reading configuration from network");
      }
    } else {
      System.err.println("No configuration URI defined");
    }
    return cfgUri;
  }




  /**
   * Check the application configuration directory for the configuration file.
   *
   * @param cfgLoc the location of the configuration file.
   * @param errMsg the buffer to append error messages to.
   * @return the URI of the configuration file, or null if it could not be found.
   * @throws TaskException if there was a problem checking the directory.
   */
  private URI checkAppCfgDirectory(String cfgLoc, StringBuffer errMsg) throws TaskException {
    URI retval = null;
    final String path = System.getProperties().getProperty(BootStrap.APP_HOME);
    if (StringUtil.isNotBlank(path)) {
      final String appDir = FileUtil.normalizePath(path);
      final File homeDir = new File(appDir);
      final File configDir = new File(homeDir, "cfg");
      if (configDir.exists()) {
        if (configDir.isDirectory()) {
          final File cfgFile = new File(configDir, cfgLoc);
          final File alternativeFile = new File(configDir, cfgLoc + RTW.JSON_EXT);

          if (cfgFile.exists()) {
            retval = FileUtil.getFileURI(cfgFile);
          } else {
            if (alternativeFile.exists()) {
              retval = FileUtil.getFileURI(alternativeFile);
            } else {
              errMsg.append(String.format("Common configuration file not found: %s", cfgFile.getAbsolutePath()) + StringUtil.CRLF);
              errMsg.append(String.format("Configuration file not found: %s", cfgLoc) + StringUtil.CRLF);
            }
          }
        } else {
          errMsg.append(String.format("Configuration directory is not a directory: %s", appDir) + StringUtil.CRLF);
        }
      } else {
        errMsg.append(String.format("Configuration directory '%s' does not exist%n", appDir));
      }
    }
    return retval;
  }




  /**
   * Check the work directory for the configuration file.
   *
   * @param cfgLoc the location of the configuration file.
   * @param errMsg the buffer to append error messages to.
   * @return the URI of the configuration file, or null if it could not be found.
   * @throws TaskException if there was a problem checking the directory.
   */
  private URI checkWorkDirectory(String cfgLoc, StringBuffer errMsg) throws TaskException {
    URI retval = null;
    if (getContext() != null && getContext().getEngine() != null) {
      File wrkDir = getContext().getEngine().getWorkDirectory();
      if (wrkDir != null) {
        if (wrkDir.exists()) {
          if (wrkDir.isDirectory()) {
            final File cfgFile = new File(wrkDir, cfgLoc);
            final File alternativeFile = new File(wrkDir, cfgLoc + RTW.JSON_EXT);

            if (cfgFile.exists() && !cfgFile.isDirectory()) {
              retval = FileUtil.getFileURI(cfgFile);
            } else {
              if (alternativeFile.exists()) {
                retval = FileUtil.getFileURI(alternativeFile);
              } else {
                errMsg.append(String.format("Work directory file not found: %s", cfgFile.getAbsolutePath()) + StringUtil.CRLF);
                errMsg.append(String.format("Configuration file not found: %s", cfgLoc) + StringUtil.CRLF);
              }
            }
          } else {
            errMsg.append(String.format("Work directory is not a directory: %s", wrkDir) + StringUtil.CRLF);
          }
        } else {
          errMsg.append(String.format("Work directory does not exist: %s", wrkDir) + StringUtil.CRLF);
        }

      } else {
        errMsg.append("Work directory not set in engine" + StringUtil.CRLF);
      }
    } else {
      errMsg.append("No reference to engine" + StringUtil.CRLF);
    }
    return retval;
  }




  /**
   * Check the current directory for the configuration file.
   *
   * @param cfgLoc the location of the configuration file.
   * @param errMsg the buffer to append error messages to.
   * @return the URI of the configuration file, or null if it could not be found.
   * @throws TaskException if there was a problem checking the directory.
   */
  private URI checkCurrentDirectory(String cfgLoc, StringBuffer errMsg) throws TaskException {
    URI retval = null;
    File localfile = new File(cfgLoc);
    File alternativeFile = new File(cfgLoc + RTW.JSON_EXT);

    if (localfile.exists() && !localfile.isDirectory()) {
      retval = FileUtil.getFileURI(localfile);
    } else {
      if (alternativeFile.exists()) {
        retval = FileUtil.getFileURI(alternativeFile);
      } else {
        errMsg.append(String.format("No local configuration file: %s%n", localfile.getAbsolutePath()));
      }
    }
    return retval;
  }




  /**
   * Run the job defined in the configuration.
   *
   * @throws TaskException if there was a problem running the job.
   */
  @Override
  protected void performTask() throws TaskException {
    final String filename = getString(ConfigTag.FILE);
    Log.debug(String.format("Reading configuration file %s", filename));
    final URI cfgUri = confirmConfigurationLocation(filename);
    Log.debug(String.format("Calculated URI of %s", cfgUri));

    if (cfgUri != null) {
      try {
        final Config jobConfig = Config.read(cfgUri);
        if (StringUtil.isBlank(jobConfig.getName())) {
          jobConfig.setName(UriUtil.getBase(cfgUri));
        }

        final Config engineConfig = jobConfig.getSection(ConfigTag.JOB);

        final TransformEngine engine = TransformEngineFactory.getInstance(engineConfig.toString());

        // if we have a name in our (RunJob) config, it overrides that in jobConfig file
        final String jobName = getString(ConfigTag.NAME);
        if (StringUtil.isNotBlank(jobName)) {
          engine.setName(jobName);
        }

        // If there was no name in the jobConfig or our (RunJob) config, set the name to the basename of the config file
        if (StringUtil.isBlank(engine.getName())) {
          engine.setName(FileUtil.getBase(cfgUri.toString()));
        }

        // place the job's context in our context under the name of the job being run
        String contextKey = engine.getName();

        // Set the engine's work directory to this task's job directory  
        engine.setWorkDirectory(getJobDirectory());

        Config params = getConfiguration().getSection(ConfigTag.PARAMETERS);
        if (params != null) {
          Log.warn(String.format("The configuration section '%s' has been deprecated. use '%s' instead", ConfigTag.PARAMETERS, ConfigTag.CONTEXT));
        } else {
          params = getConfiguration().getSection(ConfigTag.CONTEXT);
        }

        if (params != null) {
          TransformContext childContext = engine.getContext();
          if (childContext == null) {
            childContext = new TransformContext();
          }

          for (DataField field : params.getFields()) {
            String parameterName = field.getName();
            if (field.getType() == DataField.STRING) {

              // Parameters need to be resolved. They may represent context values
              String parameterValue = field.getStringValue();

              // The value of the parameter may refer to context value 
              Object obj = getContext().resolveToValue(parameterValue);
              if (obj != null) {
                childContext.set(parameterName, obj);
                Log.debug(String.format("Runjob setting parameter '%s' to context reference %s", parameterName, obj.toString()));
              } else {
                // perform a simple context resolve
                String resolvedValue = getContext().resolveToString(parameterValue);

                // If it did not result, it is probably a literal value
                if (resolvedValue == null) {
                  resolvedValue = parameterValue;
                }

                // preprocess - so any unresolved variables will be resolved in the child job
                String pval = Template.preProcess(resolvedValue, getContext().getSymbols());
                childContext.set(parameterName, pval);
                Log.debug(String.format("Runjob setting parameter '%s' to '%s'", parameterName, pval));
              }
            } else {
              childContext.set(parameterName, field.getObjectValue());
              Log.debug(String.format("Runjob setting parameter '%s' to %s", parameterName, field.getStringValue()));
            }
          } // for each parameter

          engine.setContext(childContext);
        }

        try {
          engine.run();
        } catch (NullPointerException npe) {
          String errMsg = String.format("Processing exception (NPE) running Job: %s%s", npe.getMessage(), ExceptionUtil.stackTrace(npe));

          if (haltOnError) {
            throw new TaskException(errMsg);
          } else {
            Log.error(errMsg);
            return;
          }

        } catch (final Throwable t) {
          String errMsg = String.format("Processing exception running Job: %s", t.getMessage());
          if (haltOnError) {
            throw new TaskException(errMsg);
          } else {
            Log.error(errMsg);
            return;
          }
        } finally {
          try {
            engine.close();
          } catch (final Exception ignore) {}
          getContext().set(contextKey, engine.getContext().toMap());
        }
      } catch (IOException | ConfigurationException e) {
        final String errMsg = String.format("Could not read configuration from %s - %s", cfgUri, e.getMessage());
        if (haltOnError) {
          throw new TaskException(errMsg);
        } else {
          Log.error(errMsg);
          return;
        }
      }
    } else {
      return;
    }
  }

}
