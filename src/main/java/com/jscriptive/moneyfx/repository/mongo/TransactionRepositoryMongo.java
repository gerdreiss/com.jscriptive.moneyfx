package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class TransactionRepositoryMongo implements TransactionRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void insert(Transaction transaction) {
        mongoTemplate.insert(transaction);
    }

    @Override
    public void insert(Collection<Transaction> transactions) {
        mongoTemplate.insert(transactions);
    }

    @Override
    public List<Transaction> findAll() {
        return mongoTemplate.findAll(Transaction.class);
    }

    @Override
    public List<Transaction> filterAll(TransactionFilter filter) {
        return mongoTemplate.find(FilterQueryBuilder.build(filter), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccount(Account account) {
        return mongoTemplate.find(new Query(where("account.number").is(account.getNumber())), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndYear(Account account, Integer year) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("dtOp.year").is(year)), Transaction.class);
        }
        return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("dtOp.year").is(year)), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("dtOp.year").is(year).and("dtOp.month").is(month)), Transaction.class);
        }
        return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("dtOp.year").is(year).and("dtOp.month").is(month)), Transaction.class);
    }

    @Override
    public List<Transaction> findIncomingByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").gte(ZERO)), Transaction.class);
        }
        return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").gte(ZERO)), Transaction.class);
    }

    @Override
    public List<Transaction> findOutgoingByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").lt(ZERO)), Transaction.class);
        }
        return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").lt(ZERO)), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndCategory(Account account, Category category) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("category.name").is(category.getName())), Transaction.class);
        }
        return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("category.name").is(category.getName())), Transaction.class);
    }

    @Override
    public Transaction findEarliestTransaction(Account account) {
        if (account == null) {
            return mongoTemplate.findOne(new Query().with(new Sort(ASC, "dtOp")), Transaction.class);
        }
        return mongoTemplate.findOne(new Query(where("account.number").is(account.getNumber())).with(new Sort(ASC, "dtOp")), Transaction.class);
    }

    @Override
    public int removeByAccount(Account account) {
        Query query = new Query(Criteria.where("account.bank.name").is(account.getBank().getName()).and("account.number").is(account.getNumber()).and("account.name").is(account.getName()));
        WriteResult result = mongoTemplate.remove(query, Transaction.class);
        return result.getN();
    }

    @Override
    public void update(Transaction trx) {
        // TODO implement the method
//        Query query = new Query(Criteria.where("bank.name").is(account.getBank().getName()).and("number").is(account.getNumber()).and("name").is(account.getName()));
//        Update update = Update.update("number", account.getNumber()).addToSet("name", account.getName()).addToSet("type", account.getType());
//        if (account.getBalance() != null) {
//            update = update.addToSet("balance", account.getBalance());
//            if (account.getBalanceDate() == null) {
//                update = update.addToSet("balanceDate", LocalDate.now());
//            } else {
//                update = update.addToSet("balanceDate", account.getBalanceDate());
//            }
//        }
//        mongoTemplate.updateFirst(query, update, Account.class);
    }
}
