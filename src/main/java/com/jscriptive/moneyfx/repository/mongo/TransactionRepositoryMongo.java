package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.exception.BusinessException;
import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.jscriptive.moneyfx.repository.mongo.util.NullSafeCriteriaBuilder.by;
import static java.math.BigDecimal.ZERO;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
        return mongoTemplate.find(query(by(filter)), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccount(Account account) {
        return mongoTemplate.find(query(by(account)), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndYear(Account account, Integer year) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year)), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year).and("dtOp.month").is(month)), Transaction.class);
    }

    @Override
    public List<Transaction> findIncomingByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").gte(ZERO)), Transaction.class);
    }

    @Override
    public List<Transaction> findOutgoingByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").lt(ZERO)), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndCategory(Account account, Category category) {
        return mongoTemplate.find(query(by(account).and("category.name").is(category.getName())), Transaction.class);
    }

    @Override
    public Transaction findEarliestTransaction(Account account) {
        return mongoTemplate.findOne(query(by(account)).with(new Sort(ASC, "dtOp")), Transaction.class);
    }

    @Override
    public int removeByAccount(Account account) {
        return mongoTemplate.remove(query(by(account)), Transaction.class).getN();
    }

    @Override
    public void update(Transaction trx) {
        check(trx);
        Query query = query(where("_id").is(new ObjectId(trx.getId())));
        Update update = Update.update("concept", trx.getConcept())
                .addToSet("dtOp", trx.getDtOp())
                .addToSet("dtVal", trx.getDtVal())
                .addToSet("amount", trx.getAmount())
                .addToSet("category._id", new ObjectId(trx.getCategory().getId()));

        mongoTemplate.updateFirst(query, update, Transaction.class);
    }

    private void check(Transaction trx) {
        if (trx.getId() == null) {
            throw new BusinessException("Only persisted transactions can be updated: ID must be present");
        }
        if (trx.getCategory() == null) {
            throw new BusinessException("Transactions must always be assigned to a category");
        }
        if (trx.getCategory().getId() == null) {
            throw new BusinessException("Only persisted transactions with persisted categories can be updated: category ID must be present");
        }
    }
}
