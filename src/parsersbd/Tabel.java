/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersbd;

import java.util.List;

/**
 *
 * @author Asus
 */
public class Tabel {
    private String namatabel;
    private List<String> namakolom;
    private String alias;
    private int rekord;
    private int jmlData;
    private int vdata;
    private String primarykey;
    private int jmlblok;
    


    public Tabel(String namatabel, List<String> namakolom, int rekord, int jmlData, int vdata) {
        this.namatabel = namatabel;
        this.namakolom = namakolom;
        this.rekord = rekord;
        this.jmlData = jmlData;
        this.vdata = vdata;
        this.primarykey = namakolom.get(0);
    }

    public Tabel(String namatabel, List<String> namakolom, int rekord, int jmlData, int vdata, String primarykey) {
        this.namatabel = namatabel;
        this.namakolom = namakolom;
        this.rekord = rekord;
        this.jmlData = jmlData;
        this.vdata = vdata;
        this.primarykey = primarykey;
    }
    
    
    
    

    public void setNamakolom(List<String> namakolom) {
        this.namakolom = namakolom;
    }

    public String getNamatabel() {
        return namatabel;
    }

    public String getNamakolom(int i) {
        return namakolom.get(i).toLowerCase();
    }
    
    public List<String> getListNamakolom() {
        return namakolom;
    }
    
    public void getListKolom(){
        if (!namakolom.isEmpty()){
            for (int i = 0; i<namakolom.size()-1; i++){
                System.out.print(namakolom.get(i)+", ");
            }
            System.out.print(namakolom.get(namakolom.size()-1));
        }
    }

    public String getAlias() {
        return alias;
    }
    
    public void tambahkolom(String kolom){
        this.namakolom.add(kolom);
    }

    public int getRekord() {
        return rekord;
    }

    public int getJmlData() {
        return jmlData;
    }

    public int getVdata() {
        return vdata;
    }

    public String getPrimarykey() {
        return primarykey;
    }

    public int getJmlblok() {
        return jmlblok;
    }

    public void setJmlblok(int jmlblok) {
        this.jmlblok = jmlblok;
    }
    
    
    
    
    
    
    
}
