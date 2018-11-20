
package ejb;

import java.io.*;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.MessageListener;

/**
 *
 * @author Francisco Martínez Esteso, Julián Morales Núñez
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "jms/Noticias"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/Noticias"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "jms/Noticias"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")
})
public class EjbMessageBean implements MessageListener {
    @EJB
    private EjBSessionBeanLocal ejBSessionBean;
    
    public EjbMessageBean() {
    }
    
    @Override
    public void onMessage(Message message) {
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            // Abrimos el fichero en modo append con el parámetro booleano "true".
            // https://docs.oracle.com/javase/7/docs/api/java/io/FileWriter.html
            fichero = new FileWriter("C:\\Users\\JULIÁN\\Desktop\\mensajes.txt", true);
            
            // https://docs.oracle.com/javase/7/docs/api/java/io/PrintWriter.html
            pw = new PrintWriter(fichero);
            
            if (message instanceof TextMessage) {
                TextMessage mes = (TextMessage)message;
                pw.println(mes.getText());
                ejBSessionBean.addToList(mes.getText());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fichero)
                    fichero.close();
            } 
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
}
