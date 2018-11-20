
package ejb;

import java.util.LinkedList;
import javax.ejb.Local;

/**
 *
 * @author Francisco Martínez Esteso, Julián Morales Núñez
 */
@Local
public interface EjBSessionBeanLocal {
    
    void addMsg(String m); // Publish New Message
    
    int getNumber(); // Get number of Messages
    
    LinkedList<String> getList(); // Get current list of news
    
    void addToList(String m); // Add a message to the list.
}
