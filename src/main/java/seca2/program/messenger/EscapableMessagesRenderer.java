/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.messenger;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import javax.faces.render.FacesRenderer;
import org.primefaces.component.messages.MessagesRenderer;


@FacesRenderer(componentFamily="javax.faces.Messages", rendererType="javax.faces.Messages")
public class EscapableMessagesRenderer extends MessagesRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        final ResponseWriter originalResponseWriter = context.getResponseWriter();
        context.setResponseWriter(new ResponseWriterWrapper() {

            @Override
            public ResponseWriter getWrapped() {
                return originalResponseWriter;
            }

            @Override
            public void writeText(Object text, UIComponent component, String property) throws IOException {
                String string = String.valueOf(text);
                String escape = (String) component.getAttributes().get("escape");
                if (escape != null && !Boolean.valueOf(escape)) {
                    super.write(string);
                } else {
                    super.writeText(string, component, property);
                }
            }
        });

        super.encodeEnd(context, component); // Now, render it!
        context.setResponseWriter(originalResponseWriter); // Restore original writer.
    }
}