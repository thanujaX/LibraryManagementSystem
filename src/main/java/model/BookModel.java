package model;

import dto.BookDTO;
import entity.Book;
import Util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class BookModel {

    public boolean saveBook(BookDTO dto) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            Book existingBook = session.get(Book.class, dto.getId());

            if (existingBook != null) {
                transaction.rollback();
                System.out.println("Book ISBN Already Exists!");
                return false;
            }

            Book book = new Book(
                    dto.getId(),
                    dto.getTitle(),
                    dto.getAuthor(),
                    dto.getCategory(),
                    dto.getPublishedYear(),
                    dto.getQuantity()
            );

            session.persist(book);

            transaction.commit();

            System.out.println("BOOK SAVED SUCCESSFULLY!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();
            return false;
        }
    }

    public List<BookDTO> getAllBooks() {

        List<BookDTO> bookList = new ArrayList<>();

        try (Session session = HibernateUtil.getSession()) {

            List<Book> books = session.createQuery(
                    "FROM Book ORDER BY title", Book.class
            ).getResultList();

            for (Book book : books) {

                BookDTO dto = new BookDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getPublishedYear(),
                        book.getQuantity()
                );

                bookList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookList;
    }

    public List<BookDTO> searchBooks(String searchText) {

        List<BookDTO> bookList = new ArrayList<>();

        try (Session session = HibernateUtil.getSession()) {

            String search = "%" + searchText.toLowerCase() + "%";

            List<Book> books = session.createQuery(
                            "FROM Book b WHERE " +
                                    "LOWER(b.id) LIKE :search OR " +
                                    "LOWER(b.title) LIKE :search OR " +
                                    "LOWER(b.author) LIKE :search OR " +
                                    "LOWER(b.category) LIKE :search",
                            Book.class
                    )
                    .setParameter("search", search)
                    .getResultList();

            for (Book book : books) {

                BookDTO dto = new BookDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getPublishedYear(),
                        book.getQuantity()
                );

                bookList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookList;
    }

    public boolean updateBook(BookDTO dto) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            Book book = session.get(Book.class, dto.getId());

            if (book == null) {
                transaction.rollback();
                System.out.println("Book Not Found!");
                return false;
            }

            int newQuantity = dto.getQuantity();

            int issuedQuantity =
                    book.getQuantity() - book.getAvailableQuantity();

            if (newQuantity < issuedQuantity) {
                transaction.rollback();
                System.out.println("Cannot reduce quantity below issued copies!");
                return false;
            }

            book.setTitle(dto.getTitle());
            book.setAuthor(dto.getAuthor());
            book.setCategory(dto.getCategory());
            book.setPublishedYear(dto.getPublishedYear());
            book.setQuantity(newQuantity);
            book.setAvailableQuantity(newQuantity - issuedQuantity);

            transaction.commit();

            System.out.println("BOOK UPDATED SUCCESSFULLY!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(String isbn) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            Book book = session.get(Book.class, isbn);

            if (book == null) {
                transaction.rollback();
                System.out.println("Book Not Found!");
                return false;
            }

            if (book.getAvailableQuantity() < book.getQuantity()) {
                transaction.rollback();
                System.out.println("Cannot delete! This book has issued copies.");
                return false;
            }

            session.remove(book);

            transaction.commit();

            System.out.println("BOOK DELETED SUCCESSFULLY!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();
            return false;
        }
    }

    public long getTotalBooksCount() {

        try (Session session = HibernateUtil.getSession()) {

            return session.createQuery(
                    "SELECT COUNT(b) FROM Book b",
                    Long.class
            ).getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getAvailableBooksCount() {

        try (Session session = HibernateUtil.getSession()) {

            Long count = session.createQuery(
                    "SELECT SUM(b.availableQuantity) FROM Book b",
                    Long.class
            ).getSingleResult();

            return count != null ? count : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getIssuedBooksCount() {

        try (Session session = HibernateUtil.getSession()) {

            Long count = session.createQuery(
                    "SELECT SUM(b.quantity - b.availableQuantity) FROM Book b",
                    Long.class
            ).getSingleResult();

            return count != null ? count : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}