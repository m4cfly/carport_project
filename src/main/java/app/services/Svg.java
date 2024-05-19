package app.services;

public class Svg {

    private static final String SVG_TEMPLATE = "<svg version=\"1.1\"\n" +
            "     x=\"%d\" y=\"%d\"\n" +
            "     viewBox=\"%s\" width \"%s\" \n" +
            "     preserveAspectRatio=\"xMinYMin\">"; //xMinYMin betyder den går fra top venstre til bottom højre nåår den laver aspect ratio

    private static final String SVG_RECT_TEMPLATE = "<rect x=\"%.2f\" y=\"%.2f\" height=\"%f\" width=\"%f\" style=\"%s\"/>";


    private StringBuilder svg = new StringBuilder();

    public Svg(int x, int y, String viewBox, String width){
        svg.append(String.format(SVG_TEMPLATE, x, y, viewBox, width));
    }

    public void addRectangle(double x, double y, double height, double width, String style){

        svg.append(String.format(SVG_RECT_TEMPLATE, x, y, height, width, style));


    }
    public void addLine(int x1, int y1, int x2, int y2, String style){}
    public void addArrow(int x1, int y1, int x2, int y2, String style){

        // Kald addLine med en style der indeholder pilehoveder
    }
    public void addText(int x, int y, int rotation, String text){}
    public void addSvg(Svg innerSvg){}

    @Override
    public String toString() {
        return svg.append("</svg>").toString();
    }
}
