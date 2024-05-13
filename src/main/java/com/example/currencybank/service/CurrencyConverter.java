package com.example.currencybank.service;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CurrencyConverter {
    private NodeList currencies = null;


    public CurrencyConverter() {
        try {
            URL cbr = new URL("http://www.cbr.ru/scripts/XML_daily.asp");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(cbr.openStream());
            doc.getDocumentElement().normalize();
            this.currencies = doc.getElementsByTagName("Valute");
        }
        catch(ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public double getCurrencyRate(String charCode) {
        double rate = 1;
        int nominal = 1;
        for (int i = 0; i < currencies.getLength(); i++) {
            NodeList currency = currencies.item(i).getChildNodes();
            if (currency.item(1).getTextContent().equals(charCode)) {
                String rateString = currency.item(4).getTextContent().replace(",", ".");
                rate = Double.parseDouble(rateString);
                String nominalString = currency.item(2).getTextContent();
                nominal = Integer.parseInt(nominalString);
                break;
            }
        }
        return rate/nominal;
    }

    public double getCurrencyRate(String curFrom, String curTo) {
        return getCurrencyRate(curFrom) / getCurrencyRate(curTo);
    }

    public List<String> getCurrenciesCharCodes() {
        List<String> charCodes = new ArrayList<>();
        for (int i = 0; i < currencies.getLength(); i++) {
            NodeList currency = currencies.item(i).getChildNodes();
            charCodes.add(currency.item(1).getTextContent());
        }
        return charCodes;
    }

}
