package org.opennms.web.log;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Logger;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author <a href="mailto:larry@opennms.org">Lawrence Karnowski<a>
 * @author Craig R. McClanahan (Apache Jakarta, Tomcat project)
 * @version 1.1.1.1 2001/11/11 17:36:28
 */

public class Log4JLogger implements Logger 
{

    // ----------------------------------------------------- Instance Variables


    /**
     * The Container with which this Logger has been associated.
     */
    protected Container container = null;


    /**
     * The descriptive information about this implementation.
     */
    protected static final String info =
	"org.opennms.web.log.LoggerBase/1.0";


    /** The hard-coded location of the Log4J properties file under the OpenNMS home directory. */ 
    public static final String propFilename = "/etc/log4j.properties";

    
    /**
     * The property change support for this component.
     */
    protected PropertyChangeSupport support = new PropertyChangeSupport(this);


    /**
     * The verbosity level for above which log messages may be filtered.
     */
    protected int verbosity = ERROR;

    
    /**
     * The Log4J category to log messages to.
     */
    protected Category logger;

    
    // ------------------------------------------------------------- Properties


    /**
     * Return the Container with which this Logger has been associated.
     */
    public Container getContainer() {

	return (container);

    }


    /**
     * Set the Container with which this Logger has been associated.
     *
     * @param container The associated Container
     */
    public void setContainer(Container container) {

	Container oldContainer = this.container;
	this.container = container;
	support.firePropertyChange("container", oldContainer, this.container);

    }


    /**
     * Return descriptive information about this Logger implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

	return (info);

    }


    /**
     * Return the verbosity level of this logger.  Messages logged with a
     * higher verbosity than this level will be silently ignored.
     */
    public int getVerbosity() {

	return (this.verbosity);

    }


    /**
     * Set the verbosity level of this logger.  Messages logged with a
     * higher verbosity than this level will be silently ignored.
     *
     * @param verbosity The new verbosity level
     */
    public void setVerbosity(int verbosity) {

	this.verbosity = verbosity;

    }


    /**
     * Set the verbosity level of this logger.  Messages logged with a
     * higher verbosity than this level will be silently ignored.
     *
     * @param verbosityLevel The new verbosity level, as a string
     */
    public void setVerbosityLevel(String verbosity) {

	if ("FATAL".equalsIgnoreCase(verbosity))
	    this.verbosity = FATAL;
	else if ("ERROR".equalsIgnoreCase(verbosity))
	    this.verbosity = ERROR;
	else if ("WARNING".equalsIgnoreCase(verbosity))
	    this.verbosity = WARNING;
	else if ("INFORMATION".equalsIgnoreCase(verbosity))
	    this.verbosity = INFORMATION;
	else if ("DEBUG".equalsIgnoreCase(verbosity))
	    this.verbosity = DEBUG;

    }
    
    
    /** Required parameter so we can find the Log4J config file. */    
    public void setHomeDir( String homeDir ) {
        PropertyConfigurator.resetConfiguration();
        PropertyConfigurator.configure( homeDir + Log4JLogger.propFilename );
        this.logger = Category.getInstance("OpenNMS.WEB");        
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Add a property change listener to this component.
     *
     * @param listener The listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {

	support.addPropertyChangeListener(listener);

    }


    /**
     * Writes the specified message to a servlet log file, usually an event
     * log.  The name and type of the servlet log is specific to the
     * servlet container.  This message will be logged unconditionally.
     *
     * @param message A <code>String</code> specifying the message to be
     *  written to the log file
     */
    public void log(String msg) {
        if( msg != null && msg.toUpperCase().indexOf( "DEBUG" ) > -1 ) {
            this.logger.debug( msg );
        }
        else {
            this.logger.info( msg );
        }
    }


    /**
     * Writes the specified exception, and message, to a servlet log file.
     * The implementation of this method should call
     * <code>log(msg, exception)</code> instead.  This method is deprecated
     * in the ServletContext interface, but not deprecated here to avoid
     * many useless compiler warnings.  This message will be logged
     * unconditionally.
     *
     * @param exception An <code>Exception</code> to be reported
     * @param msg The associated message string
     */
    public void log(Exception exception, String msg) {
        this.log( msg, exception );
    }


    /**
     * Writes an explanatory message and a stack trace for a given
     * <code>Throwable</code> exception to the servlet log file.  The name
     * and type of the servlet log file is specific to the servlet container,
     * usually an event log.  This message will be logged unconditionally.
     *
     * @param msg A <code>String</code> that describes the error or
     *  exception
     * @param throwable The <code>Throwable</code> error or exception
     */
    public void log(String msg, Throwable throwable) {
        if( throwable instanceof Exception ) {
            this.logger.warn( msg, throwable );
        }
        else if( throwable instanceof Error ) {
            this.logger.fatal( msg, throwable );
        }
    }


    /**
     * Writes the specified message to the servlet log file, usually an event
     * log, if the logger is set to a verbosity level equal to or higher than
     * the specified value for this message.
     *
     * @param message A <code>String</code> specifying the message to be
     *  written to the log file
     * @param verbosity Verbosity level of this message
     */
    public void log(String message, int verbosity) {

	if (this.verbosity >= verbosity)
	    log(message);

    }


    /**
     * Writes the specified message and exception to the servlet log file,
     * usually an event log, if the logger is set to a verbosity level equal
     * to or higher than the specified value for this message.
     *
     * @param message A <code>String</code> that describes the error or
     *  exception
     * @param throwable The <code>Throwable</code> error or exception
     * @param verbosity Verbosity level of this message
     */
    public void log(String message, Throwable throwable, int verbosity) {

	if (this.verbosity >= verbosity)
	    log(message, throwable);

    }


    /**
     * Remove a property change listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {

	support.removePropertyChangeListener(listener);

    }


}
