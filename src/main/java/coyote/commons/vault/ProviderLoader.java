package coyote.commons.vault;

import coyote.commons.vault.provider.Local;
import coyote.commons.minivault.util.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A ProviderLoader loads providers based on the named provider in the builder configuration.
 *
 * <p>The name of the provider is the name of the provider class to be loaded. If the classname does not appear to be
 * fully qualified, the namespace of {@code Local} provider will be used.</p>
 */
public class ProviderLoader {

  /**
   * Load the provider specified.
   *
   * <p>It is assumed this is a classname to be loaded, either simple or fully-qualified. If a simple name is used, the
   * default package for providers will be assumed.</p>
   *
   * @param className
   * @return
   */
  public static Provider loadProvider(String className) throws ProviderLoadException {
    Provider retval = null;
    if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
      className = Local.class.getPackage().getName() + "." + className;
    }
    try {
      Class<?> clazz = Class.forName(className);
      Constructor<?> ctor = clazz.getConstructor();
      Object object = ctor.newInstance();
      if (object instanceof Provider) {
        try {
          retval = (Provider) object;
        } catch (Exception e) {
          throw new ProviderLoadException(e);
        }
      } else {
        throw new ProviderLoadException("Not a vault provider");
      }
    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new ProviderLoadException("Could not create an instance of the specified provider", e);
    }
    return retval;
  }

}
