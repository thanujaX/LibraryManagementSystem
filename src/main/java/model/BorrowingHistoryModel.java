package model;

import Util.HibernateUtil;
import dto.BorrowingHistoryDTO;
import entity.IssuedBook;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class BorrowingHistoryModel {

    public List<BorrowingHistoryDTO> getAllHistory() {

        List<BorrowingHistoryDTO> dtoList = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<IssuedBook> list = session.createQuery(
                    "FROM IssuedBook i " +
                            "JOIN FETCH i.member " +
                            "JOIN FETCH i.book " +
                            "ORDER BY i.issueDate DESC",
                    IssuedBook.class
            ).list();

            for (IssuedBook issued : list) {

                dtoList.add(new BorrowingHistoryDTO(
                        issued.getIssueId(),
                        issued.getMember().getMemberId(),
                        issued.getMember().getFullName(),
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
}