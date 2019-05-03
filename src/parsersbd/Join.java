package parsersbd;

import java.util.ArrayList;
import java.util.List;

public class Join {
    List<String> tabel = new ArrayList<>();
    List<String> kolom = new ArrayList<>();

    public Join(List<String> tabel, List<String> kolom) {
        this.tabel = tabel;
        this.kolom = kolom;
    }

    public Join() {

    }

    public void setTabel(String input) {
        this.tabel.add(input);
    }

    public void setKolom(String kolom) {
        this.kolom.add(kolom);
    }

    public List<String> getTabel() {
        return tabel;
    }

    public List<String> getKolom() {
        return kolom;
    }
    
    public String getNamakolom(int i) {
        return kolom.get(i).toLowerCase();
    }

    public void setKoloms(List<String> kolom) {
        this.kolom.addAll(kolom);
    }
    
    
    
    
}
