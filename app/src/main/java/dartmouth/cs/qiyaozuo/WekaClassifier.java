package dartmouth.cs.qiyaozuo;

public class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N62589ce50(i);
        return p;
    }
    static double N62589ce50(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 78.691704) {
            p = WekaClassifier.N4efe196a1(i);
        } else if (((Double) i[0]).doubleValue() > 78.691704) {
            p = WekaClassifier.N238c24e13(i);
        }
        return p;
    }
    static double N4efe196a1(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 56.156955) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 56.156955) {
            p = WekaClassifier.N3c98fef82(i);
        }
        return p;
    }
    static double N3c98fef82(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 4.755237) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() > 4.755237) {
            p = 0;
        }
        return p;
    }
    static double N238c24e13(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 10.966008) {
            p = WekaClassifier.N5953bc14(i);
        } else if (((Double) i[64]).doubleValue() > 10.966008) {
            p = 2;
        }
        return p;
    }
    static double N5953bc14(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() <= 6.686428) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() > 6.686428) {
            p = 2;
        }
        return p;
    }
}

