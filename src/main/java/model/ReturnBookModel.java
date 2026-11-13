package model;

import Util.HibernateUtil;
import dto.IssueDTO;
import entity.Book;
import entity.IssuedBook;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnBookModel {

    public List<IssueDTO> getBorrowedBooks() {
        List<IssueDTO> dtoList = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<IssuedBook> list = session.createQuery(
                            "FROM IssuedBook i " +
                                    "JOIN FETCH i.member " +
                                    "JOIN FETCH i.book " +
                                    "WHERE i.status = :status " +
                                    "ORDER BY i.issueDate DESC",
                            IssuedBook.class)
                    .setParameter("status", "ISSUED")
                    .list();

            for (IssuedBook issued : list) {
                dtoList.add(new IssueDTO(
                        issued.getIssueId(),
                        issued.getMember().getMemberId(),
                        issued.getBook().getId(),
                        issued.getBook().getTitle(),
                        issued.getIssueDate(),
                        issued.getDueDate(),
                        issued.getReturnDate(),
                        issued.getStatus()
                ));
            }
        }

        return dtoList;
    }

    public boolean returnBook(int issueId, LocalDate returnDate) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            IssuedBook issued = session.get(IssuedBook.class, issueId);

            if (issued == null) {
                tx.rollback();
                return false;
            }

            if (issued.getStatus() != null && issued.getStatus().startsWith("RETURNED")) {
                tx.rollback();
                return false;
            }

            if (returnDate == null || returnDate.isBefore(issued.getIssueDate())) {
                tx.rollback();
                return false;
            }

            issued.setReturnDate(returnDate);

            if (returnDate.isAfter(issued.getDueDate())) {
                issued.setStatus("RETURNED LATE");
            } else {
                issued.setStatus("RETURNED ON TIME");
            }

            Book book = issued.getBook();
            if (book != null) {
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            }

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}