package br.edu.utfpr.recominer.batch;

import javax.batch.api.chunk.AbstractCheckpointAlgorithm;
import javax.inject.Named;

/**
 *
 * @author kuroda
 */
@Named
public class RecominerCheckPoint extends AbstractCheckpointAlgorithm {

    @Override
    public int checkpointTimeout() throws Exception {
        System.out.println("Checkpoint Timeout");
        return 0;
    }

    @Override
    public boolean isReadyToCheckpoint() throws Exception {
        System.out.println("IS READY");
        return true;
    }

    @Override
    public void beginCheckpoint() throws Exception {
        System.out.println("Begin Checkpoint");
    }

    @Override
    public void endCheckpoint() throws Exception {
        System.out.println("End Checkpoint");
    }

}
