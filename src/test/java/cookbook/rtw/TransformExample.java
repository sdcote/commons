package cookbook.rtw;

import coyote.commons.StringUtil;
import coyote.commons.rtw.OperationalContext;
import coyote.commons.rtw.TransformContext;

/**
 * This is a rather complex, if not complete, example how to write a transform engine using the RTW classes.
 *
 * <p>The goal of this code is to show how to write a component that will read records from somewhere, process them,
 * and optionally write them somewhere else using a set of standardized classes. This tries to utilize every feature in
 * the RTW library, so it is more complex than most components need to be.</p>
 *
 * <p>Most components will just use FrameReaders or FrameWriters to read and write records using DataFrames as the
 * abstract data type. This is a complete transformation engine. It can be even more robust, allowing for reading in
 * different configurations of readers and writers to create a complete integration framework, but that is a separate
 * project.</p>
 */
public class TransformExample implements Runnable{

    /**
     * This is the main entry point of the example.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        // CLI Options and arguments processing can go here.


        TransformExample engine = new TransformExample();

        engine.run();
    }


    @Override
    public void run() {
        // Initialize the context
        //contextInit();

    }


    private final TransformContext context = new TransformContext();


}
