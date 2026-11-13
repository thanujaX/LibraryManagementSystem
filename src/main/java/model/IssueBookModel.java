package model;

import dto.IssueDTO;
import entity.Book;
import entity.Member;
import entity.IssuedBook;
import Util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssueBookModel {

    // ISSUE BOOK
    public boolean issueBook(IssueDTO dto) throws Exception {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            // Find Member
            Member member = session.get(
                    Member.class,
                    dto.getMemberId()
            );

            if (member == null) {
                throw new Exception("Member not found!");
            }

            // Find Book
            Book book = session.get(
                    Book.class,
                    dto.getIsbn()
            );

            if (book == null) {
                throw new Exception("Book not found!");
            }

            // Check available quantity
            if (book.getAvailableQuantity() <= 0) {
                throw new Exception("Book is not available!");
            }

            // Validate dates
            if (dto.getIssueDate() == null ||
                    dto.getDueDate() == null) {
                throw new Exception("Please select issue and due dates!");
            }

            if (dto.getDueDate().isBefore(dto.getIssueDate())) {
                throw new Exception("Due date cannot be before issue date!");
            }

            // Create IssuedBook
            IssuedBook issuedBook = new IssuedBook();

            issuedBook.setMember(member);
            issuedBook.setBook(book);
            issuedBook.setIssueDate(dto.getIssueDate());
            issuedBook.setDueDate(dto.getDueDate());
            issuedBook.setReturnDate(null);
            issuedBook.setStatus("ISSUED");

            // Reduce available quantity
            book.setAvailableQuantity(
                    book.getAvailableQuantity() - 1
            );

            session.persist(issuedBook);

            transaction.commit();

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }


    // GET ALL ISSUED BOOKS
    public List<IssueDTO> getAllIssuedBooks() throws Exception {

        List<IssueDTO> list = new ArrayList<>();

        try (Session session = HibernateUtil.getSession()) {

            List<IssuedBook> issuedBooks = session.createQuery(
                    "FROM IssuedBook i " +
                            "JOIN FETCH i.member " +
                            "JOIN FETCH i.book " +
                            "ORDER BY i.issueId DESC",
                    IssuedBook.class
            ).getResultList();

            for (IssuedBook issued : issuedBooks) {

                IssueDTO dto = new IssueDTO(
                        issued.getIssueId(),
                        issued.getMember().getMemberId(),
                        issued.getBook().getId(),
                        issued.getBook().getTitle(),
                        issued.getIssueDate(),
                        issued.getDueDate(),
                        issued.getReturnDate(),
                        issued.getStatus()
                );

                list.add(dto);
            }
        }

        return list;
    }


    // RETURN BOOK
    public boolean returnBook(
            int issueId,
            LocalDate returnDate
    ) throws Exception {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            IssuedBook issuedBook = session.createQuery(
                            "FROM IssuedBook i " +
                                    "JOIN FETCH i.book " +
                                    "WHERE i.issueId = :id",
                            IssuedBook.class
                    )
                    .setParameter("id", issueId)
                    .uniqueResult();

            if (issuedBook == null) {
                throw new Exception("Issued book not found!");
            }

            if (!"ISSUED".equalsIgnoreCase(issuedBook.getStatus())) {
                throw new Exception("This book has already been returned!");
            }

            if (returnDate == null ||
                    returnDate.isBefore(issuedBook.getIssueDate())) {
                throw new Exception("Invalid return date!");
            }

            Book book = issuedBook.getBook();

            // Set return date
            issuedBook.setReturnDate(returnDate);

            // Update status
            if (returnDate.isAfter(issuedBook.getDueDate())) {
                issuedBook.setStatus("RETURNED LATE");
            } else {
                issuedBook.setStatus("RETURNED ON TIME");
            }

            // Increase available quantity
            book.setAvailableQuantity(
                    book.getAvailableQuantity() + 1
            );

            transaction.commit();

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }
}