/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote;

import coyote.commons.minivault.MiniVault;
import coyote.commons.snap.AbstractSnapJob;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

/**
 * This is a Snap job that launches the MiniVault from the Snap Job scripts
 */
public class Vault extends AbstractSnapJob {
    @Override
    public void start() {
        final CountDownLatch latch = new CountDownLatch(1);

        MiniVault.main(new String[]{});

        // Attach a listener to the window to signal when it is closed.
        // We use invokeLater to ensure we are interacting with the GUI
        // after it has been initialized by MiniVault.main().
        SwingUtilities.invokeLater(() -> {
            attachCloseListener(latch);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Finds the active MiniVault window and attaches a closure listener.
     * @param latch The synchronization barrier to release upon closure.
     */
    private void attachCloseListener(CountDownLatch latch) {
        // Find the JFrame created by MiniVault.
        // Note: This assumes MiniVault creates at least one Frame.
        Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (frame instanceof JFrame && frame.isVisible()) {
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        latch.countDown();
                    }
                });
                return;
            }
        }

        // Safety check: If no window was found, don't block the thread forever
        latch.countDown();
    }

    @Override
    public void stop() {}

}
