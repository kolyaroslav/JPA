package jpa1;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class App {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: Add Apartments");
                    System.out.println("2: View apartments ");
                    System.out.println("3: Delete Apartments");
                    System.out.println("4: Filter apartments");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addApartments(sc);
                            break;
                        case "2":
                            viewApartments();
                            break;
                        case "3":
                            deleteApartments(sc);
                            break;
                        case "4":
                            filterApartmetns(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void addApartments(Scanner sc) {
        System.out.print("Enter region: ");
        String region = sc.nextLine();
        System.out.print("Enter address of apartments: ");
        String address = sc.nextLine();
        System.out.print("Enter area of apartment: ");
        String sAre = sc.nextLine();
        int area = Integer.parseInt(sAre);
        System.out.print("Enter rooms q-ty: ");
        String sRoo = sc.nextLine();
        int rooms = Integer.parseInt(sRoo);
        System.out.print("Enter apartment price in USD: ");
        String sPri = sc.nextLine();
        int price = Integer.parseInt(sPri);




        em.getTransaction().begin();
        try {
            Apartments c = new Apartments(region,address,area,rooms,price);
            em.persist(c);
            em.getTransaction().commit();

            System.out.println(c.getId());
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void deleteApartments(Scanner sc) {
        System.out.print("Enter apartment id: ");
        String aId = sc.nextLine();
        long id = Long.parseLong(aId);

        Apartments c = em.getReference(Apartments.class, id);
        if (c == null) {
            System.out.println("Apartment not found!");
            return;
        }

        em.getTransaction().begin();
        try {
            em.remove(c);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }



    private static void viewApartments() {
        Query query = em.createQuery("SELECT c FROM Apartments c", Apartments.class);
        List<Apartments> list = (List<Apartments>) query.getResultList();

        for (Apartments c : list)
            System.out.println(c);
    }

    private static void filterApartmetns(Scanner sc) {
        System.out.println("Choose filter");
        System.out.println("1: By price");
        System.out.println("2: By district");
        System.out.println("3: By area");
        System.out.println("4: By number of rooms");
        System.out.print("-> ");

        String s = sc.nextLine();

        List<Apartments> list = null;
        String filter = "";

        switch (s) {
            case "1":
                list = priceFilter(sc);
                filter = "By Price";
                break;
            case "2":
                list = districtFilter(sc);
                filter = "By District";
                break;
            case "3":
                list = areaFilter(sc);
                filter = "By Area";
                break;
            case "4":
                list = roomNumberFilter(sc);
                filter = "By Number of Rooms";
                break;
            default:
                return;
        }

        System.out.printf("\nFilter: %s: \n____________________________\n", filter);
        if (list != null) {
            for (Apartments a : list) {
                System.out.println(a);
            }
        }
        System.out.println("____________________________");

    }

    private static List<Apartments> priceFilter(Scanner sc) {
        System.out.println("Your budget: \n ");
        System.out.println("from:");
        String sMinPrice = sc.nextLine();
        System.out.println("to:");
        String sMaxPrice = sc.nextLine();

        int minPrice = Integer.parseInt(sMinPrice);
        int maxPrice = Integer.parseInt(sMaxPrice);

        List<Apartments> list = null;

        try {
            Query query = em.createQuery(
                    "SELECT x FROM Apartments x WHERE x.price >:minPrice AND x.price <:maxPrice");
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);

            list = (List<Apartments>) query.getResultList();

        } catch (NoResultException ex) {
            System.out.println("Not found!");
            return null;
        }

        em.getTransaction().begin();
        try {

            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }

        return list;

    }

    private static List<Apartments> districtFilter(Scanner sc) {
        System.out.print("Press the number of the district of apartments from the list below: \n ");
        System.out.println("1. Golosievo\n 2. Dniprovskiy\n 3. Shevchenkivsky\n 4. Solomiansky\n");
        String district = checkDistrict(sc);

        List<Apartments> list = null;

        try {
            Query query = em.createQuery("SELECT x FROM Apartments x WHERE x.district =:district");
            query.setParameter("district", district);

            list = (List<Apartments>) query.getResultList();

        } catch (NoResultException e) {
            System.out.println("Apartment not found!");
            return null;
        }

        em.getTransaction().begin();
        try {
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }

        return list;

    }

    private static List<Apartments> areaFilter(Scanner sc) {
        System.out.println("filter the area you want: \n ");
        System.out.println("from:");
        String sMinArea = sc.nextLine();
        System.out.println("to:");
        String sMaxArea = sc.nextLine();

        int minArea = Integer.parseInt(sMinArea);
        int maxArea = Integer.parseInt(sMaxArea);

        List<Apartments> list = null;

        try {
            Query query = em.createQuery(
                    "SELECT x FROM Apartments x WHERE x.area >:minArea AND x.area <:maxArea");
            query.setParameter("minArea", minArea);
            query.setParameter("maxArea", maxArea);

            list = (List<Apartments>) query.getResultList();

        } catch (NoResultException e) {
            System.out.println("Apartment not found!");
            return null;
        }

        em.getTransaction().begin();
        try {
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }

        return list;
    }

    private static List<Apartments> roomNumberFilter(Scanner sc) {
        System.out.println("How many rooms do you want?  ");
        String sNumber = sc.nextLine();

        int roomNumber = Integer.parseInt(sNumber);

        List<Apartments> list = null;

        try {
            Query query = em.createQuery(
                    "SELECT x FROM Apartments x WHERE x.roomNumber =:roomNumber");
            query.setParameter("roomNumber", roomNumber);

            list = (List<Apartments>) query.getResultList();

        } catch (NoResultException e) {
            System.out.println("Apartment not found!");
            return null;
        }

        em.getTransaction().begin();
        try {
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }

        return list;
    }


    private static String checkDistrict(Scanner sc){
        String s = sc.nextLine();
        int number = Integer.parseInt(s);

        switch (number){
            case 1: s = "Golosievo";
                break;
            case 2: s = "Dniprovskiy";
                break;
            case 3: s = "Shevchenkivsky";
                break;
            case 4: s = "Solomiansky";
                break;
            default: return null;
        }
        return s;
    }






}









