/**
 * This is a set of classes to allow easy reading, transforming, and writing of data records.
 *
 * <p>Read, Transform, and Write (RTW) is a design pattern that allows for structured processing of records of various
 * types. The concept centers on a transformer that is written by the developer to process data on a record-by-record
 * basis. The Transformer reads a record from a reader, processes it in some manner, then uses a writer to write the
 * record out in another format.</p>
 */
package coyote.commons.rtw;