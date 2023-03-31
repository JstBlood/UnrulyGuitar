package server.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import commons.Card;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;


public class TestCardRepository implements CardRepository {

    public final List<Card> cards = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    private Optional<Card> find(Long id) {
        return cards.stream().filter(q -> q.id == id).findFirst();
    }

    @Override
    public List<Card> findAll() {
        calledMethods.add("findAll");
        return cards;
    }

    @Override
    public List<Card> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Card> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Card> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Card> S saveAndFlush(S entity) {
        calledMethods.add("saveAndFlush");
        cards.set(entity.index, entity);
        return entity;
    }

    @Override
    public <S extends Card> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Card> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public Card getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Card getById(Long id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public <S extends Card> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Card> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Card> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Card> S save(S entity) {
        call("save");
        entity.id = cards.size();
        cards.add(entity);
        return entity;
    }

    @Override
    public Optional<Card> findById(Long id) {
        return find(id);
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return cards.size();
    }

    @Override
    public void deleteById(Long id) {
        call("deleteById");
        Optional<Card> card = find(id);
        card.ifPresent(cards::remove);
    }

    @Override
    public void delete(Card entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends Card> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Card> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Card> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Card> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends Card> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends Card, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void shiftCardsUp(int index, long listId) {
        call("shiftCardsUp");
        cards.stream()
                .filter(c -> c.parentCardList.id == listId)
                .filter(c -> c.index > index)
                .forEach(c -> c.id--);
    }

    @Override
    public void shiftCardsDown(int index, long listId) {
        call("shiftCardsDown");
        cards.stream()
                .filter(c -> c.parentCardList.id == listId)
                .filter(c -> c.index > index)
                .forEach(c -> c.id++);
    }

}