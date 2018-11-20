package remoto;

import javax.jms.*;
import javax.naming.*;
import java.util.Scanner;

/**
 *
 * @author Francisco Martínez Esteso, Julián Morales Núñez
 */
public class MsgConexion {

    public String nombreCola = "jms/Noticias"; 
    public Context contexto = null; // Contexto JNDI
    public TopicConnectionFactory factoria = null; 
    public TopicConnection conexionCola = null; 
    public TopicSession sesionCola = null; 
    public Topic cola = null; 

    public MsgConexion() {
    }

    public static void main(String[] args){
        try {
            // Obtenemos el contexto JNDI actual
            InitialContext iniCtx = new InitialContext();
            
            // Buscamos los recursos sobre él.
            TopicConnectionFactory tcf = (TopicConnectionFactory) iniCtx.lookup("jms/FactoriaConexiones");
            Topic t = (Topic) iniCtx.lookup("jms/Noticias");
            
            // Creamos la conexión sobre la factoria de conexiones.
            TopicConnection conexion = tcf.createTopicConnection();
            
            // Creamos el publisher sobre la sesión.
            TopicSession sesion = conexion.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicPublisher publisher = sesion.createPublisher(t);
            conexion.start(); 
            
            // Creamos el subscriber y su escucha
            TopicSubscriber subscriber = sesion.createSubscriber(t);
            subscriber.setMessageListener(new MessageListener() {
                
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        try {
                            String recibido = null;
                            TextMessage mes = (TextMessage) message;
                            recibido = mes.getText();
                            System.out.println("Mensaje recibido:" + recibido);
                        } catch (JMSException ex) {
                        }  
                    }
                }
            });
            
            String mensaje = null;
            do {
                System.out.println("Introduce mensaje: ");
                
                // https://docs.oracle.com/javase/7/docs/api/java/util/Scanner.html
                Scanner scan = new Scanner(System.in);
                mensaje = scan.nextLine();
                TextMessage msj = sesion.createTextMessage();
                msj.setText(mensaje);
                publisher.publish(msj);
            } while (!"exit".equals(mensaje));
            publisher.close();
            conexion.close();
        } catch (Exception ex) {}
    }
}
