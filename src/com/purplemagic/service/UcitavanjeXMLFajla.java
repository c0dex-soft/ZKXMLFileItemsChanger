package com.purplemagic.service;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.select.annotation.Listen;

import java.io.File;
import java.util.ArrayList;


public class UcitavanjeXMLFajla {


	// Lista koja sadrzi spisak svih stavki iz XML fajla
	private ArrayList<String> spisakStavki = new ArrayList<String>();

	public ArrayList<String> getSpisakStavki() {
		return spisakStavki;
	}

	public void setSpisakStavki(ArrayList<String> spisakStavki) {
		this.spisakStavki = spisakStavki;
	}

	// Naziv fajla koji se u�?itava
	private String nazivFajla;

	public String getNazivFajla() {
		return nazivFajla;
	}

	public void setNazivFajla(String nazivFajla) {
		this.nazivFajla = nazivFajla;
	}

	// Stavka koja se dodaje preko pop-up prozora dodajPopup.zul
	private String nazivStavkeZaDodavanje;

	public String getnazivStavkeZaDodavanje() {
		return nazivStavkeZaDodavanje;
	}

	public void setnazivStavkeZaDodavanje(String nazivStavkeZaDodavanje) {
		this.nazivStavkeZaDodavanje = nazivStavkeZaDodavanje;
	}

	private String nazivStavkeZaIzmenu;

	public String getnazivStavkeZaIzmenu() {
		return nazivStavkeZaIzmenu;
	}

	public void setnazivStavkeZaIzmenu(String nazivStavkeZaIzmenu) {
		this.nazivStavkeZaIzmenu = nazivStavkeZaIzmenu;
	}

	// Selektovana stavka
	private String selektovanaStavka;

	public String getSelektovanaStavka() {
		return selektovanaStavka;
	}

	public void setSelektovanaStavka(String selektovanaStavka) {
		this.selektovanaStavka = selektovanaStavka;
	}

	// Identifikator za vidljivost prozora za dodavanje stavke
	public boolean prozorDodaj = false;

	public boolean isProzorDodaj() {
		return prozorDodaj;
	}

	public void setProzorDodaj(boolean prozorDodaj) {
		this.prozorDodaj = prozorDodaj;
	}


	// Identifikator za vidljivost prozora za izmenu stavke
	public boolean prozorIzmeni = false;

	public boolean isProzorIzmeni() {
		return prozorIzmeni;
	}

	public void setProzorIzmeni(boolean prozorIzmeni) {
		this.prozorIzmeni = prozorIzmeni;
	}

	// U�?itavanja XML fajla u program
	@Command
	@NotifyChange({"spisakStavki", "nazivFajla"})
	public void ucitajXML() {

		spisakStavki.clear();
	    try {

			File XMLFile = new File("WebContent/doc/Finansijsko knjigovodstvo.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XMLFile);

			nazivFajla = XMLFile.getName();

			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("type");

			System.out.println("----------------------------");

			for (int e = 0; e < nList.getLength(); e++) {

				Node nNode = nList.item(e);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					System.out.println("Stavka." +e+ ": " + nNode.getTextContent());
					spisakStavki.add(nNode.getTextContent());
				}
			}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Greska: " + e);
	    }
	    System.out.println(spisakStavki);
	}


	// Upisivanje stavki u XML fajl
	@Command
	public void upisiXML() {
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// definisanje korena elemenata
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("documentTypes");
			doc.appendChild(rootElement);
			
			
			for (int stavka=0; stavka < spisakStavki.size(); stavka++) {
				Element type = doc.createElement("type");
				type.appendChild(doc.createTextNode(spisakStavki.get(stavka)));
				rootElement.appendChild(type);
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("WebContent/doc/Finansijsko knjigovodstvo.xml"));			

			transformer.transform(source, result);
			Messagebox.show("XML fajl je sačuvan!");
			
		} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
		} catch (TransformerException tfe) {
				tfe.printStackTrace();
		}

	}
	
	
	// Dodavanje stavke (spisakStavki - lista stavki; nazivStavkeZaDodavanje - stavka koju dodajemo preko popup dijaloga; prozorDodaj - identifikator vidljivost popupa za dodavanje stavke)
	@Command
	@NotifyChange({"spisakStavki", "nazivStavkeZaDodavanje", "prozorDodaj"})
	@Listen("onClick = #dodajStavku")
	public void dodajStavku() {
		nazivStavkeZaDodavanje = "";
		prozorDodaj = true;
		nazivStavkeZaDodavanje = "";
	}

	// Dodavanje stavke u popup dijalogu
	@Command
	@NotifyChange({"spisakStavki", "prozorDodaj"})
	public void dodaj() {
		if (nazivStavkeZaDodavanje != "") {
			spisakStavki.add(nazivStavkeZaDodavanje);
			prozorDodaj = false;
		} else
			Messagebox.show("Niste uneli naziv stavke koju želite dodati");
		System.out.println(spisakStavki);
	}
	
	// Izmena stavke
	@Command
	@NotifyChange({"spisakStavki", "nazivStavkeZaIzmenu", "prozorIzmeni"})
	@Listen("onClick = #izmeniStavku")
	public void izmeniStavku() {
		nazivStavkeZaIzmenu = selektovanaStavka;
		prozorIzmeni = true;
	}
	
	// Izmena stavke u popup dijalogu
	@Command
	@NotifyChange ({"spisakStavki", "prozorIzmeni"})
	public void izmeni() {
		if (nazivStavkeZaIzmenu != "") {
			int index = spisakStavki.indexOf(selektovanaStavka);
			spisakStavki.set(index, nazivStavkeZaIzmenu);
		} else {
			Messagebox.show("Niste uneli novi naziv stavke");
		}		
		System.out.println(spisakStavki);
		prozorIzmeni = false;
	}

	
	// Prikazivanje popup dijaloga za brisanje stavke
	@Command
	@NotifyChange("spisakStavki")
	public void popupIzbrisi() {
		Object[] odgovor = { "Da", "Ne" };
		int odabraniOdgovor = JOptionPane.showOptionDialog(null, "Da li ste sigurni da želite da uklonite stavku sa liste?", "Uklanjanje stavke sa liste", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, odgovor, odgovor[0]);
		if (odabraniOdgovor == 0)
			izbrisi();
	}
	
	// Brisanje stavke
	@Command
	@NotifyChange("spisakStavki")
	public void izbrisi() {
		spisakStavki.remove(selektovanaStavka);
	}

	// Odustajanje od dodavanja/izmene stavke
	@Command
	@NotifyChange({"prozorDodaj", "prozorIzmeni"})
	public void odustani() {
		selektovanaStavka = null;
		nazivStavkeZaDodavanje = null;
		nazivStavkeZaIzmenu = null;
		prozorDodaj = false;
		prozorIzmeni = false;
	}

}