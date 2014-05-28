/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.template;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

/**
 *
 * @author vincent.a.lee
 */
public class HeadRenderer extends Renderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        context.getResponseWriter().startElement("head", component);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // NOOP.
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        for (UIComponent resource : context.getViewRoot().getComponentResources(context, "head")) {
            resource.encodeAll(context);
        }

        context.getResponseWriter().endElement("head");
    }

}