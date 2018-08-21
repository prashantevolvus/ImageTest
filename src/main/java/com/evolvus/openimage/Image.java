/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evolvus.openimage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.BasicTwoWayMatcher;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.MatchingUtilities;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.shape.Polygon;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.model.fit.RANSAC;

/**
 *
 * @author Prashant Maroli @ Evolvus
 */
public class Image {

    public static void main(String[] args) {
        System.out.println("Hello World");
        try {
            //MBFImage image = ImageUtilities.readMBF(new File("photo1.jpg"));
            //FImage imageF = ImageUtilities.readF(new File("photo.jpg"));
            //ImageUtilities.write(imageF,"JPEG",new File("output_open.jpg"));
            //ImageUtilities.write(imageF,"TIFF",new File("output_open1.tiff"));

            MBFImage query = ImageUtilities.readMBF(new File("query.jpg"));
            MBFImage target = ImageUtilities.readMBF(new File("mandate.jpg"));

            DoGSIFTEngine engine = new DoGSIFTEngine();
            LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
            LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());

            LocalFeatureMatcher<Keypoint> matcher = new BasicTwoWayMatcher<Keypoint>();

            RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(5.0, 1500,
                    new RANSAC.PercentageInliersStoppingCondition(0.5));
            matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                    new FastBasicKeypointMatcher<Keypoint>(8), modelFitter);

            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);

            MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(),
                    RGBColour.RED);
            Polygon rct = (Polygon)query.getBounds().transform(modelFitter.getModel().getTransform().inverse());
            System.out.println(rct.getVertices().toArray()[0]);
            List<Point2d> l = rct.getVertices();
            int ilen = (int)l.get(0).getX() - (((int)l.get(1).getX() - (int)l.get(0).getX())/4);
            target.drawLine(ilen, (int)l.get(0).getY(), (int)l.get(0).getX(), (int)l.get(0).getY(),3,RGBColour.BLUE);
           // DisplayUtilities.display(target.flatten()); 
            target.drawShape(
                    query.getBounds().transform(modelFitter.getModel().getTransform().inverse()), 3, RGBColour.BLUE);
            DisplayUtilities.display(target);
            //image.processInplace(new CannyEdgeDetector());
            // DisplayUtilities.display(image);
            // DisplayUtilities.display(imageF);
        } catch (IOException ex) {
            Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
