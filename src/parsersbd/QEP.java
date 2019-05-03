package parsersbd;

import com.sun.xml.internal.ws.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class QEP {
    private List<Tabel> tabel;
    private List<String> query;
    private String queryaja;
    private int br;
    private int h;
    private int b;
    private Cost kombinasilistCost;

    
    public QEP(List<Tabel> tabel, List<String> query, int h) {
        this.tabel = tabel;
        this.query = query;
        this.br = tabel.get(0).getJmlblok();
        this.h = h;
        this.b = tabel.get(0).getJmlblok();
        
    }
    
    //menampilkan semua hasil kemungkinan qep
    public void hasilQEP(List<Tabel> tabel, List<String> query){
        List<Cost> listCost = new ArrayList<>();
        int tt;
        int ts;
        int inib;
        String kolomsama;
        String output = "";
        int i = 0;
        if (!possibleOperation(query).isEmpty()){
            if (!cekJoin(query)){   
                for(String kondisi: kondisiSelect(query)){        
                    for(String s : possibleOperation(new ArrayList<String>(Arrays.asList(kondisi.split(" "))))){
                        i++;
                        System.out.println("QEP#"+i);
                        output = "PROJECTION " + tampilKolom(tabel) +" -- on the fly"+ "\n";
                        output = output + "SELECTION " + kondisi + "-- " + s +"\n";
                        output = output+ tabel.get(0).getNamatabel() + "\n";
                        inib = this.b;
                        if (s.equals("A3") || s.equals("A5")){
                            boolean numeric = true;
                            int num = inib;
                            try {
                                num = Integer.parseInt(kondisi.split(" ")[2]);
                            } catch (NumberFormatException e) {
                                numeric = false;
                            }

                            if(numeric){
                                inib = num;
                            }
                        }
                        tt = HitungCost(s, this.br, this.h, inib, "tt", "");
                        ts = HitungCost(s, this.br, this.h, inib, "ts", "");
                        output =  output+ "Cost : "+tt+" blok transfer dan "+ts+" seek";
                        listCost.add(new Cost("QEP#"+i, tt, ts, output, getQueryaja()));
                        System.out.println(output);
                        System.out.println("");
                        output = "";
                    }
                }
            }else if (cekJoin(query)){
                if (possibleOperation(query).get(0).equals("bnjoin") && this.tabel.size()== 2){
                    for (String s : kombinasiTabel(cekJoin(query), tabel)){
                        i++;
                        System.out.println("QEP#"+i);
                        output = "PROJECTION " + tampilKolom(tabel) +" -- on the fly"+ "\n" ;
                        kolomsama = cariKolom(this.tabel.get(0), this.tabel.get(1));
                        output = output + "JOIN " + this.tabel.get(0).getNamatabel() + "."+kolomsama+" = "+ this.tabel.get(1).getNamatabel()+"."+kolomsama+" -- BNLJ" + "\n";
                        output = output+ s + "\n" ;
                        tt = HitungCost("bnjoin", this.br, this.h, this.b, "tt", s);
                        ts = HitungCost("bnjoin", this.br, this.h, this.b, "ts", s);
                        output = output+ "Cost (worst case): "+tt+" blok transfer dan "+ts+" seek";
                        listCost.add(new Cost("QEP#"+i, tt, ts, output, getQueryaja()));
                        System.out.println(output);
                        System.out.println("");
                        output = "";
                    }
                }
                
            }
            System.out.println("QEP optimal : "+OptimalSolution(listCost).getNumber());
            setKombinasilistCost(OptimalSolution(listCost));
        }else{
            System.out.println("QEP");
            System.out.println("PROJECTION " + tampilKolom(tabel) + " -- on the fly");
            System.out.println(tabel.get(0).getNamatabel());
            
        }
        
        
    }

    public void setQueryaja(String queryaja) {
        this.queryaja = queryaja;
    }

    public String getQueryaja() {
        return queryaja;
    }

    public Cost getKombinasilistCost() {
        return kombinasilistCost;
    }

    public void setKombinasilistCost(Cost kombinasilistCost) {
        this.kombinasilistCost = kombinasilistCost;
    }
    
    public void showALL(List<Cost> input){
        for (Cost c : input){
            System.out.println(c.getNumber()+"\n"+c.getQuery()+"\n"+c.getTulisan()+"\n"+c.getTt()+c.getTs());
        }
    }
    
    //menentukan qep yang paling optimal
    public Cost OptimalSolution(List<Cost> input){
        Cost mintt = input.get(0);
        Cost mints = input.get(0);
        for(Cost c : input){
            if (c.getTt() < mintt.getTt()){
                mintt = c;
            }
            if (c.getTs() < mints.getTs()){
                mints = c;
            }
        }
        
        if (mintt.getNumber().equals(mints.getNumber())){
            return mintt;
        }else{
            if (Math.abs(mintt.getTt()-mints.getTt()) > Math.abs(mintt.getTs()-mints.getTs()) ){
                return mintt;
                
            }else{
                return mints;
            }
        }
        
        
    }
    
    public class Cost{
        private String number;
        private int tt;
        private int ts;
        private String tulisan;
        private String query;

        public Cost(String number, int tt, int ts, String tulisan, String query) {
            this.number = number;
            this.tt = tt;
            this.ts = ts;
            this.tulisan = tulisan;
            this.query = query;
        }

        public void setTt(int tt) {
            this.tt = tt;
        }

        public void setTs(int ts) {
            this.ts = ts;
        }

        public int getTt() {
            return tt;
        }

        public int getTs() {
            return ts;
        }

        public String getNumber() {
            return number;
        }

        public String getTulisan() {
            return tulisan;
        }

        public String getQuery() {
            return query;
        }
        
        
  
    }
    
    //mengembalikan kemungkinan operasi yang dilakukan
    public List<String> possibleOperation(List<String> input){
        List<String> operation = new ArrayList<>();  
        if(cekEquality(input) && primaryKey(input)){
            operation.add("A1k");
            operation.add("A2");
            operation.add("A4k");
        }else if (cekComparison(input) && primaryKey(input)){
            operation.add("A5");
            operation.add("A1");
        }else if (cekEquality(input) && !primaryKey(input)){
            operation.add("A3");
            operation.add("A1");
        }else if (cekJoin(input)){
            operation.add("bnjoin");
        } 
        return operation;
    }
    
    //mengembalikan apakah kondisi query itu equality atau tidak
    private boolean cekEquality(List<String> input){
         boolean output = false;
         for(String s: input){
             if (s.equals("=")){
                 output = true;
             }
         }
         return output;
     }
    
    //mengembalikan apakah kondisi query itu comparison atau tidak
    private boolean cekComparison(List<String> input){
         boolean output = false;
         for(String s: input){
             if (s.contains(">") || s.contains("<")){
                 output = true;
             }
         }
         return output;
     }
     
    //mengembalikan apakah kondisi query itu menggunakan key atau tidak
    private boolean primaryKey(List<String> input){
        if (input.get(0).equals(this.tabel.get(0).getPrimarykey())){
            return true;
        }else{
            return false;
        }
     } 
    
    //mengembalikan apakah query itu join atau tidak
    private boolean cekJoin(List<String> input){
         boolean output = false;
         for(String s: input){
             if (s.equals("join")){
                
                 output = true;
             }
         }
         return output;
     } 
    
    //menampilkan kolom yang di prjection query
    public String tampilKolom(List<Tabel> tabel){
         String output = "";
         for(Tabel t : tabel){
             for(String s : t.getListNamakolom()){
                 output = output + s;
                 if (!s.equals(t.getListNamakolom().get(t.getListNamakolom().size()-1))){
                     output = output + ", ";
                 }
             }
         }
         return output;
     }
    
    //membuat kondisi selection query apa saja
    public List<String> kondisiSelect(List<String> input){
        boolean ambil = false;
        String output = "";
        List<String> listkondisi = new ArrayList<>();
        int j = 0;
        for(int i = 0; i < input.size(); i++){  
            if (ambil && j <= 3){
                j++;
                output = output + input.get(i)+ " ";
                if (j == 3){
                    listkondisi.add(output);
                    ambil = false;
                }
            }
            if(input.get(i).equals("where") || input.get(i).equals("and") || input.get(i).equals("or")){
                j = 0;
                ambil = true;
                output = "";
            }
        }
        return listkondisi;
     }
    
    //membuat list kemungkinan variasi kombinasi tabel saat join
    public List<String> kombinasiTabel(boolean join, List<Tabel> tabel){
        List<String> list = new ArrayList<>();
        if (join && tabel.size()==2){
            list.add(getTabel(0).getNamatabel()+" "+getTabel(1).getNamatabel());
            list.add(getTabel(1).getNamatabel()+" "+getTabel(0).getNamatabel());
        }
        return list;
    }
    
    //mengembalikan nilai blok transfer atau seeks
    public int HitungCost(String operation, int br, int h, int b, String t, String kombinasi){
        int ts = 1;
        int tt = 1;
        int tt_abs = 1;
        int ts_abs = 1;
        if(operation.equals("A1")){
            tt = tt_abs*br;
        }else if (operation.equals("A1k")){
            tt = (br/2)*tt_abs;
        }else if (operation.equals("A2") || operation.equals("A4k")){
            ts = (h+1)*ts_abs;
            tt = (h+1)*tt_abs;
        }else if (operation.equals("A3") || operation.equals("A5")){
            b--;
            ts = h*ts_abs;
            tt = (b*tt_abs)+(h*tt_abs);
        }else if(operation.equals("bnjoin")){
            if (this.tabel.size() == 2){
                if (kombinasi.startsWith(this.tabel.get(0).getNamatabel())){
                    tt = this.tabel.get(0).getJmlblok()* this.tabel.get(1).getJmlblok() + this.tabel.get(0).getJmlblok();
                    ts = 2*this.tabel.get(0).getJmlblok();
                }else{
                    tt = this.tabel.get(1).getJmlblok()* this.tabel.get(0).getJmlblok() + this.tabel.get(1).getJmlblok();
                    ts = 2*this.tabel.get(1).getJmlblok();
                }
            }
        }
        if(t.equals("tt")){
            return tt;
        }else{
            return ts;
        }
    }
    
    public String cariKolom(Tabel a, Tabel b){
        String output = "";
        for(String c: a.getListNamakolom()){
            for(String d : b.getListNamakolom()){
                if (c.equals(d)){
                    output = c;
                }
            }
        }
        return output;
    }

    public Tabel getTabel(int i) {
        return tabel.get(i);
    }
    
    
    
}
