# Things To Do

## Snap

## Bootstrap

If it is necessary to name a snap job, use the following:
```java
if (StringUtil.isBlank(configuration.getName())) {
  configuration.setName(UriUtil.getBase(cfgUri));
}
```

## MLLP (Minimal Lower Layer Protocol)

Support the ability to test HL7 integrations using TCP/MLLP: The standard for HL7 v2.

**Channels:** These are the primary units of work. A channel consists of a Source (where data comes from), Filters (rules to decide if a message should be processed), Transformers (logic to modify data), and one or more Destinations (where data is sent).

Channels map to a Snap Job in that a Reader operates as a Source, and Writers operate as Destinations. RTW components can be used to filter and transform.

### MLLP Reader

### MLLP Writer