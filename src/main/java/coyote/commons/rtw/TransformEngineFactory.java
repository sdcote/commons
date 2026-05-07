/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.template.SymbolTable;
import coyote.commons.template.Template;
import coyote.commons.cfg.Config;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.log.Log;
import coyote.commons.rtw.aggregate.AbstractFrameAggregator;
import coyote.commons.rtw.context.ContextListener;
import coyote.commons.rtw.context.OperationalContext;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.filter.AbstractFrameFilter;
import coyote.commons.rtw.listener.AbstractListener;
import coyote.commons.rtw.mapper.AbstractFrameMapper;
import coyote.commons.rtw.mapper.DefaultFrameMapper;
import coyote.commons.rtw.reader.AbstractFrameReader;
import coyote.commons.rtw.task.AbstractTransformTask;
import coyote.commons.rtw.transform.AbstractFrameTransform;
import coyote.commons.rtw.validate.AbstractValidator;
import coyote.commons.rtw.writer.AbstractFrameWriter;
import coyote.commons.vault.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


/**
 * This factory uses a JSON string to create and configure the proper instance of a Transform engine.
 *
 * <p>The returned engine must be opened and closed by the caller.</p>
 *
 * <p>Once opened, the caller can invoke {@code run()} to run the engine, transforming all data in the source.</p>
 */
public class TransformEngineFactory {

  /**
   * Constant to assist in determining the full class name of readers
   */
  private static final String READER_PKG = AbstractFrameReader.class.getPackage().getName();
  /**
   * Constant to assist in determining the full class name of writers
   */
  private static final String WRITER_PKG = AbstractFrameWriter.class.getPackage().getName();
  /**
   * Constant to assist in determining the full class name of listeners
   */
  private static final String LISTENER_PKG = AbstractListener.class.getPackage().getName();
  /**
   * Constant to assist in determining the full class name of tasks
   */
  private static final String TASK_PKG = AbstractTransformTask.class.getPackage().getName();
  /**
   * Constant to assist in determining the full class name of validators
   */
  private static final String VALIDATOR_PKG = AbstractValidator.class.getPackage().getName();
  /**
   * Constant to assist in determining the full class name of frame transformers
   */
  private static final String TRANSFORM_PKG = AbstractFrameTransform.class.getPackage().getName();
  /**
   * Constant to assist in determining the full class name of frame mappers
   */
  private static final String MAPPER_PKG = AbstractFrameMapper.class.getPackage().getName();
  /**
   * Constant to assist in determining the full class name of frame filters
   */
  private static final String FILTER_PKG = AbstractFrameFilter.class.getPackage().getName();
  /**
   * Constant to assist in determining the full class name of frame aggregators
   */
  private static final String AGGREGATOR_PKG = AbstractFrameAggregator.class.getPackage().getName();

  private static final String LOCAL = "Local";
  private static final String FILE = "File";

  /**
   * Read a JSON string in from a file and create a transformation engine to the specifications in the file.
   *
   * @param cfgFile File containing the JSON configuration
   * @return an engine ready to run the transformation
   */
  public static TransformEngine getInstance(File cfgFile) {
    String configuration = FileUtil.fileToString(cfgFile);
    return getInstance(configuration);
  }


  /**
   * Create a Transformation engine configured to the specification provided in the given configuration string.
   *
   * @param cfg The JSON string specifying the configuration
   * @return an engine ready to run the transformation
   */
  public static TransformEngine getInstance(String cfg) {
    TransformEngine retval = null;

    List<DataFrame> config = JSONMarshaler.marshal(cfg);

    if (config != null && config.size() > 0) {
      DataFrame frame = config.get(0);

      retval = getInstance(frame);

    }

    return retval;
  }


  /**
   * Create a Transformation engine configured to the specification provided in the given configuration frame.
   *
   * <p>This will determine what component to load in the engine and uses {@code ConfigurableComponent} to configure
   * each component.</p>
   *
   * @param frame The DataFrame containing the configuration
   * @return an engine ready to run the transformation
   */
  public static TransformEngine getInstance(DataFrame frame) {
    if (Log.isLogging(Log.DEBUG_EVENTS)) Log.debug("Engine Factory " + RTW.VERSION + " creating engine instance.");
    TransformEngine retval = null;

    if (frame != null) {

      retval = new DefaultTransformEngine();

      for (DataField field : frame.getFields()) {

        if (StringUtil.equalsIgnoreCase(ConfigTag.READER, field.getName())) {
          if (field.isFrame()) {
            configReader((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid reader configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.VAULT, field.getName())) {
          if (field.isFrame()) {
            configVault((DataFrame) field.getObjectValue());
          } else {
            Log.error("Invalid vault configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.PRELOAD, field.getName())) {
          if (field.isFrame()) {
            configPreloader((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid filters configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.FILTER, field.getName())) {
          if (field.isFrame()) {
            configFilters((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid filters configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.MAPPER, field.getName())) {
          if (field.isFrame()) {
            configMapper((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid mapper configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.AGGREGATOR, field.getName())) {
          if (field.isFrame()) {
            configAggregator((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid aggregator configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.WRITER, field.getName())) {
          if (field.isFrame()) {
            configWriter((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid writer configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.VALIDATE, field.getName())) {
          if (field.isFrame()) {
            configValidation((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid validate configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.TRANSFORM, field.getName())) {
          if (field.isFrame()) {
            configTransformer((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid transform configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.PREPROCESS, field.getName()) || StringUtil.equalsIgnoreCase(ConfigTag.TASK, field.getName())) {
          if (field.isFrame()) {
            configPreProcess((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid pre-process configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.POSTPROCESS, field.getName())) {
          if (field.isFrame()) {
            configPostProcess((DataFrame) field.getObjectValue(), retval);
          } else {
            Log.error("Invalid post-process configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.CONTEXT, field.getName())) {
          if (field.isFrame()) {
            configContext(new Config((DataFrame) field.getObjectValue()), retval);
          } else {
            Log.error("Invalid context configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.CLASS, field.getName())) {
          // ignore the CLASS field...it is used by the Loader, but not by us.
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.LISTENER, field.getName())) {
          if (field.isFrame()) {
            DataFrame cfgFrame = (DataFrame) field.getObjectValue();
            if (cfgFrame != null) {
              // there can be many listeners
              if (cfgFrame.isArray()) {
                for (DataField cfgfield : cfgFrame.getFields()) {
                  if (cfgfield.isFrame()) {
                    configListener((DataFrame) cfgfield.getObjectValue(), retval);
                  } else {
                    Log.error("Invalid listener configuration section");
                  }
                }
              } else {
                configListener(cfgFrame, retval);
              }
            } // null / empty check
          } else {
            Log.error("Invalid listener configuration section");
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.NAME, field.getName())) {
          if (field.isFrame()) {
            Log.error("Invalid Name value - expecting simple type (string)");
          } else {
            retval.setName(field.getStringValue());
          }
        } else if (StringUtil.equalsIgnoreCase(ConfigTag.SCHEDULE, field.getName())) {
          if (!field.isFrame()) {
            Log.error("Invalid Schedule section - expecting complex type");
          }
        } else {
        Log.debug(String.format("Unrecognized configuration section '%s'", field.getName()));
        }
      }
    }
    return retval;
  }


  /**
   * Create a vault and place it in the Template fixture.
   *
   * @param cfg the configuration for the vault
   */
  private static void configVault(DataFrame cfg) {
    if (cfg != null) {
      VaultBuilder builder = new VaultBuilder();
      for (DataField field : cfg.getFields()) {
        if (ConfigTag.PROVIDER.equalsIgnoreCase(field.getName())) {
          builder.setProvider(field.getStringValue());
        } else if (ConfigTag.METHOD.equalsIgnoreCase(field.getName())) {
          builder.setMethod(field.getStringValue());
        } else if (ConfigTag.SOURCE.equalsIgnoreCase(field.getName())) {
          builder.setSource(field.getStringValue());
        } else {
          builder.setProperty(field.getName(), field.getStringValue());
        }
      }


      // if no provider, assume local
      if (StringUtil.isBlank(builder.getProvider())) builder.setProvider(LOCAL);

      // if no method && local provider, assume file
      if (StringUtil.isBlank(builder.getMethod()) && LOCAL.equalsIgnoreCase(builder.getProvider()))
        builder.setMethod(FILE);

      try {
        Vault vault = builder.build();
        Template.putStatic(VaultProxy.LOOKUP_TAG, new VaultProxy(vault));
      } catch (ConfigurationException e) {
        Log.error("Vault configuration exception",e);
      } catch (VaultException e) {
        Log.error(String.format("Could not open vault: %s", e.getMessage()), e);
      }
    } else {
      Log.error("Missing vault configuration. No vault created.");
    }
  }


  /**
   * Configure the preloader
   *
   * @param cfg    the configuration section to use in configuring the components
   * @param engine the engine to configure
   */
  private static void configPreloader(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      // Make sure the class is fully qualified 
      String className = findString(ConfigTag.CLASS, cfg);
      if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
        className = READER_PKG + "." + className;
        cfg.put(ConfigTag.CLASS, className);
      } else {
        Log.error("NO Reader Class in configuration: " + cfg.toString());
      }
      Object object = RTW.createComponent(cfg);
      if (object != null) {
        if (object instanceof FrameReader) {
          engine.setPreloader((FrameReader) object);
          Log.debug(String.format("EngineFactory created preloader %s", object.getClass().getName()));
        } else {
          Log.error(String.format("Specified class (%s) is not a preloader", object.getClass().getName()));
        }
      } else {
        Log.error(String.format("Could not create an instance of specified preloader %s", className));
      }
    } // cfg !null    
  }


  /**
   * Configure the aggregator
   *
   * @param cfg    the configuration section to use in configuring the components
   * @param engine the engine to configure
   */
  private static void configAggregator(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      // Make sure the class is fully qualified 
      String className = findString(ConfigTag.CLASS, cfg);
      if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
        className = AGGREGATOR_PKG + "." + className;
        cfg.put(ConfigTag.CLASS, className);
      }
      Object object = RTW.createComponent(cfg);
      if (object != null) {
        if (object instanceof FrameAggregator) {
          engine.addAggregator((FrameAggregator) object);
          Log.debug(String.format("EngineFactory created aggregator %s", object.getClass().getName()));
        } else {
          Log.error(String.format("Specified class (%s) is not an aggregator", object.getClass().getName()));
        }
      } else {
        Log.error(String.format("Could not create an instance of specified aggregator %s", className));
      }
    } // cfg !null    
  }


  /**
   * Use the given configuration frame to add validators to the engine.
   *
   * @param cfg    the dataframe containing the validators
   * @param engine the engine to configure with the validators
   */
  private static void configValidation(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      for (DataField field : cfg.getFields()) {
        String className = field.getName();
        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
          className = VALIDATOR_PKG + "." + className;
        }

        // All tasks must have an object(frame) as its value.
        if (field.isFrame()) {
          DataFrame validatorConfig = (DataFrame) field.getObjectValue();
          Object object = RTW.createComponent(className, validatorConfig);
          if (object != null) {
            if (object instanceof FrameValidator) {
              engine.addValidator((FrameValidator) object);
              Log.debug(String.format("EngineFactory created validator %s with configuration: %s", object.getClass().getName(), validatorConfig));
            } else {
              Log.error(String.format("Specified class (%s) is not a validator", field.getName()));
            }
          } else {
            Log.error(String.format("Could not create instance of specified validator: %s", className));
          }
        } else {
          Log.error(String.format("Frame validator did not contain valid configuration: %s", field.getStringValue()));
        }
      } // for each validator
    } // cfg !null
  }


  /**
   * The Transform section is a little bit different from the rest. The section
   * is a group of named frames.
   *
   * <p>This structure is because there are many different transforms applied,
   * unlike there being just one reader, or mapper, etc.</p>
   * @param cfg
   * @param engine
   */
  private static void configTransformer(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      for (DataField field : cfg.getFields()) {
        String className = field.getName();
        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
          className = TRANSFORM_PKG + "." + className;
        }

        // All tasks must have an object(frame) as its value.
        if (field.isFrame()) {
          DataFrame transformerConfig = (DataFrame) field.getObjectValue();
          Object object = RTW.createComponent(className, transformerConfig);
          if (object != null) {
            if (object instanceof FrameTransform) {
              engine.addTransformer((FrameTransform) object);
              Log.debug(String.format("EngineFactory created frame transformer %s with configuration: %s", object.getClass().getName(), transformerConfig));
            } else {
              Log.error(String.format("Specified class (%s) was not a transformer", field.getName()));
            }
          } else {
            Log.error(String.format("Could not create an instance of specified transformer: %s", className));
          }
        } else {
          Log.error(String.format("Transformer task did not contain valid configuration: %s", field.getStringValue()));
        }
      } // for each transformer
    } // cfg !null
  }


  private static void configFilters(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      for (DataField field : cfg.getFields()) {
        String className = field.getName();
        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
          className = FILTER_PKG + "." + className;
        }

        // All filters must have an object(frame) as its value.
        if (field.isFrame()) {
          DataFrame taskConfig = (DataFrame) field.getObjectValue();
          Object object = RTW.createComponent(className, taskConfig);
          if (object != null) {
            if (object instanceof FrameFilter) {
              int seq = engine.addFilter((FrameFilter) object);
              Log.debug(String.format("EngineFactory created filter %s (seq: %d) with configuration: %s", object.getClass().getName(), seq, cfg));
            } else {
              Log.error(String.format("Specified class (%s) is not a filter", object.getClass().getName()));
            }
          } else {
            Log.error(String.format("Could not create filter: %s", className));
          }
        } else {
          Log.error(String.format("Filter configuration not a section: %s", field.getStringValue()));
        }
      } // for each task
    } // cfg !null
  }


  private static void configPreProcess(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      for (DataField field : cfg.getFields()) {
        String className = field.getName();
        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
          className = TASK_PKG + "." + className;
        }

        // All tasks must have an object(frame) as its value.
        if (field.isFrame()) {
          DataFrame taskConfig = (DataFrame) field.getObjectValue();
          Object object = RTW.createComponent(className, taskConfig);
          if (object != null) {
            if (object instanceof TransformTask) {
              int seq = engine.addPreProcessTask((TransformTask) object);
              Log.debug(String.format("EngineFactory created preprocess task %s (seq: %d) with configuration: %s", object.getClass().getName(), seq, taskConfig));
            } else {
              Log.error(String.format("Preprocess class (%s) is not a transform task", object.getClass().getName()));
            }
          } else {
            Log.error(String.format("Could not create preprocess task: %s", className));
          }
        } else {
          Log.error(String.format("Preprocess task configuration not a section: %s", field.getStringValue()));
        }
      } // for each task
    } // cfg !null
  }


  private static void configPostProcess(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      for (DataField field : cfg.getFields()) {
        String className = field.getName();
        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
          className = TASK_PKG + "." + className;
        }

        // All tasks must have an object(frame) as its value.
        if (field.isFrame()) {
          DataFrame taskConfig = (DataFrame) field.getObjectValue();
          Object object = RTW.createComponent(className, taskConfig);
          if (object != null) {
            if (object instanceof TransformTask) {
              int seq = engine.addPostProcessTask((TransformTask) object);
              Log.debug(String.format("EngineFactory created postprocess task %s (seq: %d) with configuration: %s", object.getClass().getName(), seq, cfg));
            } else {
              Log.error("Specified post-process class was not a transform task");
            }
          } else {
            Log.error(String.format("Could not create an instance of the specified post-process task: %s", className));
          }
        } else {
          Log.error(String.format("Post-process task did not contain a configuration, only scalar: %s", field.getStringValue()));
        }
      } // for each task 
    } // cfg !null
  }


  /**
   * This section of the configuration should be simply name-value pairs.
   *
   * <p>The values are treated as templates and parsed into their final values based on the contents of the symbol
   * table and the systems properties contained therein.</p>
   *
   * <p>The name value pairs are placed in the symbol table as well as the context so that results of processing can be
   * used in subsequent templates.</p>
   *
   * <p>If the configuration contains a "class" attribute, then a custom context will be loaded from that class name
   * and passed the configuration section to configure it further. In such cases, custom context classes use the
   * "fields" attribute to populate the context, by convention. If no class attribute is given, each of the attributes
   * are loaded into the default context as described above.</p>
   *
   * @param cfg    the configuration frame
   * @param engine the transform engine
   */
  private static void configContext(Config cfg, TransformEngine engine) {

    if (cfg != null) {
      TransformContext context = engine.getContext();

      if (context == null) {
        if (cfg.contains(ConfigTag.CLASS)) {
          String className = cfg.getAsString(ConfigTag.CLASS);
          if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
            className = OperationalContext.class.getPackage().getName() + "." + className;
            cfg.put(ConfigTag.CLASS, className);
          }

          try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            Object object = ctor.newInstance();

            if (object instanceof TransformContext) {
              try {
                context = (TransformContext) object;
                context.setConfiguration(cfg);
                engine.setContext(context);
                context.setEngine(engine);
                Log.debug(String.format("EngineFactory created custom context %s", context.getClass().getName()));
              } catch (Exception e) {
                Log.error(String.format("Could not configure specified context %s - %s: %s", object.getClass().getName(), e.getClass().getSimpleName(), e.getMessage()));
              }
            } else {
              Log.warn(String.format("Specified context (%s) is not a transform context", className));
            }
          } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Log.error(String.format("Could not create instance of specified context %s - %s: %s", className, e.getClass().getName(), e.getMessage()));
          }
        } else {
          // this is a regular, in-memory context with these settings
          context = new TransformContext();
          context.setConfiguration(cfg);
          engine.setContext(context);
          context.setEngine(engine);
          Log.debug(String.format("EngineFactory created context %s", context.getClass().getName()));
        }
        Log.debug(String.format("EngineFactory loaded context %s", context.getClass().getName()));
      } else {
        Log.warn("Could not replace existing context");
      }
    } // cfg !null
  }


  private static void configWriter(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      // Make sure the class is fully qualified 
      String className = findString(ConfigTag.CLASS, cfg);
      if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
        className = WRITER_PKG + "." + className;
        cfg.put(ConfigTag.CLASS, className);
      }
      Object object = RTW.createComponent(cfg);
      if (object != null) {
        if (object instanceof FrameWriter) {
          engine.addWriter((FrameWriter) object);
          Log.debug(String.format("EngineFactory created writer %s", object.getClass().getName()));
        } else {
          Log.error(String.format("Specified class (%s) is not a writer", object.getClass().getName()));
        }
      } else {
        Log.error(String.format("Could not create instance of specified writer: %s", className));
      }
    } // cfg !null
  }


  private static void configMapper(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      String className = findString(ConfigTag.CLASS, cfg);

      // If there is no class tag, use the default mapper class
      if (className == null) {
        cfg.put(ConfigTag.CLASS, DefaultFrameMapper.class.getCanonicalName());
      } else {
        // make sure the class name is fully qualified
        if (StringUtil.countOccurrencesOf(className, ".") < 1) {
          className = MAPPER_PKG + "." + className;
          cfg.put(ConfigTag.CLASS, className);
        }
      }

      Object object = RTW.createComponent(cfg);
      if (object != null) {
        if (object instanceof FrameMapper) {
          engine.setMapper((FrameMapper) object);
          Log.debug(String.format("EngineFactory created mapper %s", object.getClass().getName()));
        } else {
          Log.error(String.format("Specified class (%s) is not a frame mapper", object.getClass().getName()));
        }
      } else {
        Log.error(String.format("Could not create specified mapper: %s", className));
      }
    } // cfg !null
  }


  private static void configReader(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      // Make sure the class is fully qualified 
      String className = findString(ConfigTag.CLASS, cfg);
      if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
        className = READER_PKG + "." + className;
        cfg.put(ConfigTag.CLASS, className);
      } else {
        Log.error("NO Reader Class in configuration: " + cfg.toString());
      }
      Object object = RTW.createComponent(cfg);
      if (object != null) {
        if (object instanceof FrameReader) {
          engine.setReader((FrameReader) object);
          Log.debug(String.format("EngineFactory created reader %s", object.getClass().getName()));
        } else {
          Log.error(String.format("Specified class (%s) is not a reader", object.getClass().getName()));
        }
      } else {
        Log.error(String.format("Could not create instance of specified reader: %s", className));
      }
    } // cfg !null
  }


  /**
   * Configure the all the listeners.
   *
   * <p>This method expects listeners to be formatted in a manner similar to
   * the following:<pre>"Listeners": {
   *   "DataProfiler": { "target": "dataprofile.txt" }
   * }</pre>
   *
   * @param cfg    The entire data frame containing the listener configurations
   * @param engine The engine to which the configured listeners will be added.
   */
  private static void configListener(DataFrame cfg, TransformEngine engine) {
    if (cfg != null) {
      for (DataField field : cfg.getFields()) {
        String className = field.getName();
        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
          className = LISTENER_PKG + "." + className;
        }

        // All listeners must have an object(frame) as its value.
        if (field.isFrame()) {
          DataFrame listenerConfig = (DataFrame) field.getObjectValue();
          Object object = RTW.createComponent(className, listenerConfig);
          if (object != null) {
            if (object instanceof ContextListener) {
              engine.addListener((ContextListener) object);
              Log.debug(String.format("EngineFactory created listener %s", object.getClass().getName()));
            } else {
              Log.error(String.format("Specified class (%s) is not a listener", object.getClass().getName()));
            }
          } else {
            Log.error(String.format("Could not create instance of specified listener: %s", className));
          }
        } else {
          Log.error(String.format("Listener configuration invalid: %s", field.getStringValue()));
        }
      } // for each listener 
    } // cfg !null
  }


  /**
   * Convenience method to perform a case insensitive search for a named field
   * in a data frame and return its value as a string.
   *
   * @param name  the name of the field to search
   * @param frame the data frame in which to search
   * @return the string value of the first found field with that name or null if the field is null, the name is null or
   * the field with that name was not found.
   */
  private static String findString(String name, DataFrame frame) {
    if (name != null) {
      for (DataField field : frame.getFields()) {
        if (StringUtil.equalsIgnoreCase(name, field.getName())) {
          return field.getStringValue();
        }
      }
    }
    return null;
  }

}
