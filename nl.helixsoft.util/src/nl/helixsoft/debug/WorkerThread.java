package nl.helixsoft.debug;

/**
 * A method that is potentially blocking, because it performs a long-running operation
 * (such as network or disk access). 
 * Calls to this method must be wrapped in a SwingWorker, or done from a worker thread.
 * They should not be done from the EDT.
 */
public @interface WorkerThread
{

}
