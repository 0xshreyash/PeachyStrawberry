package com.comp30022.helium.strawberry.ar;

import com.comp30022.helium.strawberry.components.ar.ARArrowManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//import static org.mockito.Mockito.mock;


public class ARArrowManagerTest {
    ARArrowManager arrowManager;

    @Before
    public void setUp() throws Exception {
/*        ARModelImporter arModelImporter = new ARModelImporter();
        arModelImporter.loadFromAsset("narrow.armodel");
        ARModelNode arrowModelNode = arModelImporter.getNode();

        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset("target.png");

        ARLightMaterial material = new ARLightMaterial();
        material.setTexture(texture2D);
        material.setAmbient(.8f, .8f, .8f);

        for (ARMeshNode m : arrowModelNode.getMeshNodes()) {
            m.setMaterial(material);
        }
        arrowModelNode.scaleByUniform(100f);
        this.arrowManager = new ARArrowManager(null, null, arrowModelNode);*/
    }

    @After
    public void tearDown() throws Exception {
        // reset arrow manager
        this.arrowManager = null;
    }

    @Test
    public void testGetDirectionalVector() throws Exception {
/*        Method directionalCalculator = this.arrowManager
                .getClass()
                .getDeclaredMethod("getDirectionalVector");
        directionalCalculator.setAccessible(true);
        // everything after this.arrowManager is the parameters into directionalCalculator
        Vector2f unitVector = (Vector2f)directionalCalculator.invoke(this.arrowManager, true);*/
    }

}