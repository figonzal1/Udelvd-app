package cl.udelvd.servicios;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static VolleySingleton instancia;
    private RequestQueue requestQueue;
    private final Context context;

    /**
     * Constructor private de clase
     *
     * @param context Contexto de app
     */
    private VolleySingleton(Context context) {
        this.context = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    /**
     * Funcion publica de instancia de Volley
     *
     * @param context Contexto de app
     * @return Instancia de Volley
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        //Si la instancia no existe
        if (instancia == null) {
            instancia = new VolleySingleton(context);
        }
        return instancia;
    }

    /**
     * Funcion que devuelve la cola de request's
     *
     * @return retorna la cola de solicitudes apiladas en volley
     */
    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, Object tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }


}
