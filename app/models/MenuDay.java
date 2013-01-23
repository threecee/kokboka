package models;


public enum MenuDay {
     Mandag, Tirsdag, Onsdag, Torsdag, Fredag, Lørdag, Søndag;

    public static MenuDay fromIndex(int index)
    {
        switch (index) {
            case 1: return Mandag;
            case 2: return Tirsdag;
            case 3: return Onsdag;
            case 4: return Torsdag;
            case 5: return Fredag;
            case 6: return Lørdag;
            case 0: return Søndag;
        }
        return null;
    }

}
