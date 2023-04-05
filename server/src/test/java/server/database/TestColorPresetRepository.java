/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.database;

import commons.ColorPreset;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class TestColorPresetRepository implements ColorPresetRepository {

    private final List<ColorPreset> presets = new ArrayList<>();
    private final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    private Optional<ColorPreset> find(Long id) {
        return presets.stream().filter(q -> q.id == id).findFirst();
    }

    public List<ColorPreset> getPresets() {
        return presets;
    }

    public List<String> getCalled() {
        return calledMethods;
    }

    public void clean() {
        presets.clear();
        calledMethods.clear();
    }

    @Override
    public List<ColorPreset> findAll() {
        calledMethods.add("findAll");
        return presets;
    }

    @Override
    public List<ColorPreset> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ColorPreset> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends ColorPreset> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends ColorPreset> S saveAndFlush(S entity) {
        calledMethods.add("saveAndFlush");
        return entity;
    }

    @Override
    public <S extends ColorPreset> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<ColorPreset> entities) {
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
    public ColorPreset getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ColorPreset getById(Long id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public <S extends ColorPreset> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends ColorPreset> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<ColorPreset> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends ColorPreset> S save(S entity) {
        call("save");
        entity.id = presets.size();
        presets.add(entity);
        return entity;
    }

    @Override
    public Optional<ColorPreset> findById(Long id) {
        call("findById");
        return find(id);
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return presets.size();
    }

    @Override
    public void deleteById(Long id) {
        call("deleteById");
        Optional<ColorPreset> cardList = find(id);
        cardList.ifPresent(presets::remove);
    }

    @Override
    public void delete(ColorPreset entity) {
        presets.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends ColorPreset> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends ColorPreset> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends ColorPreset> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends ColorPreset> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends ColorPreset> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends ColorPreset, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}