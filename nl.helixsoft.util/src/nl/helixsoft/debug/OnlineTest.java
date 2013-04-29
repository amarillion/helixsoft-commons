package nl.helixsoft.debug;

/** 
 * An online test uses a webservice and a working internet connection.
 * 
 * Online tests should be treated differently than unit tests.
 * 
 * The test should be skipped if a working internet connection is not available. 
 * 
 * An occasional failure of an online test could be due to server issues and should not
 * be treated as a fatal failure. Only repeated failures over a period of weeks should be 
 * seen as a signal that there is a bug in the code, or an API incompability.
 * */

public @interface OnlineTest
{

}
