package com.keltron.petshop.services.impl;

import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class WatermarkPageEvent extends PdfPageEventHelper {

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

    	PdfContentByte canvas = writer.getDirectContentUnder();

    	Font watermarkFont = FontFactory.getFont(
    	        FontFactory.HELVETICA_BOLD,
    	        48,
    	        Font.NORMAL,
    	        new Color(220, 220, 220));

    	float centerX = document.getPageSize().getWidth() / 2;
    	float centerY = document.getPageSize().getHeight() / 2;
    	float gap = 45;   // Increase this if needed

    	ColumnText.showTextAligned(
    	        canvas,
    	        Element.ALIGN_CENTER,
    	        new Phrase("KERALA STATE", watermarkFont),
    	        centerX,
    	        centerY + gap,
    	        45);

    	ColumnText.showTextAligned(
    	        canvas,
    	        Element.ALIGN_CENTER,
    	        new Phrase("ANIMAL WELFARE BOARD", watermarkFont),
    	        centerX,
    	        centerY - gap,
    	        45);
    }
}