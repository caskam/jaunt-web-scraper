package com.company;

import com.jaunt.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Main {

    // URL to visit, as defined by the first argument
    private static String url;

    // Exact Image Width and Image Height required for the image to be a candidate test data point
    private static String imageWidth;
    private static String imageHeight;

    public static void main(String[] args) {
        System.out.println("A web scraper built using the jaunt api");

        // Criteria for validating image dimensions
        if (args.length > 0)
            url = args[0];
        else
            return;

        imageHeight = "768";
        imageWidth = "1024";

        // Set up UserAgent to visit sites
        UserAgent userAgent = new UserAgent();
        visitSite(userAgent);
    }

    /**
     *
     * Visits site and writes images to file
     *
     * @param userAgent : Jaunt UserAgent that acts as client visiting site
     */
    private static void visitSite(UserAgent userAgent) {

        try {
            userAgent.visit(url);
            ArrayList<URL> urls = scrapeImages(userAgent.doc.findEach("<img>"));
            writeImagesToFile(urls);
        } catch (JauntException e) {
            System.err.println(e);
        }
    }

    /**
     *
     * Finds all images on a web page that meet dimensional criteria
     *
     * @param imageElements : all html image tags that exist on a webpage
     * @return : ArrayList of acceptable image src urls
     */
    private static ArrayList<URL> scrapeImages(Elements imageElements) {

        ArrayList<URL> urls = new ArrayList<URL>();
        URL url;
        for (Element image: imageElements
                ) {
                try {
                    if (
                            (!image.hasAttribute("height") || !image.hasAttribute("width"))
                                    || (image.getAt("height").equals(imageHeight) || image.getAt("width").equals(imageWidth))) {

                        System.out.println("Image was not an acceptable data point and was rejected because it did not meet dimension criteria.");
                    } else {

                        url =  new URL(image.getAt("src"));
                        urls.add(url);

                    }

                } catch (JauntException jauntException) {
                    System.err.println("Jaunt Exception: " + jauntException);
                } catch (MalformedURLException malformedURLException) {
                    System.err.println("MalformedURL Exception: " + malformedURLException);
                } catch (NullPointerException nullPointerException) {
                    System.err.println("NullPointer Exception: " + nullPointerException);
                }
            }
        return urls;
        }

    /**
     *
     * Writes image sources from webpage to file in jpg
     *
     * @param urls : image src links that will be written to file and saved
     */
    private static void writeImagesToFile(ArrayList<URL> urls) {
        BufferedImage image;
        int i = 0;
        try {
            for (URL url : urls
                    ) {
                image = ImageIO.read(url);

                ImageIO.write(image, "jpg", new File("images/" + "scraped-image-" + String.valueOf(i) + ".jpg"));
                i++;
            }
        } catch (IOException ioException) {
            System.err.println("IO Exception: " + ioException);
        }
    }
}
