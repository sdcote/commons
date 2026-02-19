# Things To Do

## Snap

## Bootstrap

If it is necessary to name a snap job, use the following:
```java
if (StringUtil.isBlank(configuration.getName())) {
  configuration.setName(UriUtil.getBase(cfgUri));
}
```