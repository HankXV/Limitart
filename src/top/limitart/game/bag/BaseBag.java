/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.limitart.game.bag;

import top.limitart.base.Alpha;
import top.limitart.base.Test1;

import java.util.*;
import java.util.Map.Entry;

/**
 * 包裹
 *
 * @author hank
 */
@Alpha
@Deprecated
public abstract class BaseBag {
    private static final int START_GRID = 0;

    /**
     * 是否能添加物品
     *
     * @param item
     * @return
     * @throws ItemZeroNumException
     * @throws BagFullException
     * @throws ItemNotExistException
     */
    public void canAddItem(BaseBagItem item) throws ItemZeroNumException, BagFullException, ItemNotExistException {
        if (item == null) {
            throw new ItemNotExistException();
        }
        if (item.getNum() <= 0) {
            throw new ItemZeroNumException();
        }
        if (remain() <= 0) {
            throw new BagFullException();
        }
    }

    /**
     * 能否批量添加物品
     *
     * @param items
     * @return
     * @throws ItemZeroNumException
     * @throws BagFullException
     * @throws ItemNotExistException
     */
    public void canAddItems(List<BaseBagItem> items)
            throws ItemZeroNumException, BagFullException, ItemNotExistException {
        if (items.isEmpty()) {
            throw new ItemNotExistException();
        }
        for (BaseBagItem item : items) {
            if (item.getNum() <= 0) {
                throw new ItemZeroNumException();
            }
        }
        if (remain() < items.size()) {
            throw new BagFullException();
        }
    }

    /**
     * 添加一个物品
     *
     * @param gridId
     * @param item
     * @return
     * @throws BagFullException
     * @throws ItemZeroNumException
     * @throws ItemNotExistException
     */
    public void addItem(int gridId, BaseBagItem item)
            throws BagFullException, ItemZeroNumException, ItemNotExistException {
        if (gridId < START_GRID || gridId > capacity() - 1 || bag().containsKey(gridId)) {
            gridId = getAnEmptyGrid();
        }
        if (gridId == -1) {
            throw new BagFullException();
        }
        canAddItem(item);
        bag().put(gridId, item);
        onItemAdded(gridId, item);
    }

    /**
     * 添加一个物品
     *
     * @param item
     * @return
     * @throws BagFullException
     * @throws BagGridOccupiedException
     * @throws ItemZeroNumException
     * @throws ItemNotExistException
     */
    public void addItem(BaseBagItem item)
            throws BagFullException, ItemZeroNumException, ItemNotExistException {
        addItem(Integer.MIN_VALUE, item);
    }

    /**
     * 添加一串物品
     *
     * @param items
     * @return
     * @throws BagFullException
     * @throws BagGridOccupiedException
     * @throws ItemNotExistException
     * @throws ItemZeroNumException
     */
    public void addItems(List<BaseBagItem> items)
            throws BagFullException, BagGridOccupiedException, ItemNotExistException, ItemZeroNumException {
        if (items.isEmpty()) {
            throw new ItemNotExistException();
        }
        canAddItems(items);
        for (BaseBagItem item : items) {
            addItem(item);
        }
    }

    /**
     * 删除某物品
     *
     * @param gridId
     * @return
     */
    public BaseBagItem removeItem(int gridId) {
        BaseBagItem remove = bag().remove(gridId);
        if (remove != null) {
            onItemRemoved(gridId, remove);
        }
        return remove;
    }

    /**
     * 根据条件删除相关物品
     *
     * @param filter
     * @return
     */
    public List<BaseBagItem> removeItems(Test1<BaseBagItem> filter) {
        List<BaseBagItem> result = new ArrayList<>();
        Iterator<Entry<Integer, BaseBagItem>> iterator = bag().entrySet().iterator();
        for (; iterator.hasNext(); ) {
            Entry<Integer, BaseBagItem> next = iterator.next();
            if (filter.test(next.getValue())) {
                iterator.remove();
                result.add(next.getValue());
                onItemRemoved(next.getKey(), next.getValue());
            }
        }
        return result;

    }

    /**
     * 获取相应格子的物品
     *
     * @param gridId
     * @return
     */
    public BaseBagItem getItem(int gridId) {
        return bag().get(gridId);
    }

    /**
     * 获取符合条件的物品
     *
     * @param filter
     * @return
     */
    public List<BaseBagItem> getItems(Test1<BaseBagItem> filter) {
        List<BaseBagItem> result = new ArrayList<>();
        for (BaseBagItem item : bag().values()) {
            if (filter.test(item)) {
                result.add(item);
            }
        }
        return result;

    }

    /**
     * 获取符合条件的物品的数量
     *
     * @param filter
     * @return
     */
    public int getItemCount(Test1<BaseBagItem> filter) {
        int count = 0;
        for (BaseBagItem item : bag().values()) {
            if (filter.test(item)) {
                count += item.getNum();
            }
        }
        return count;

    }

    /**
     * 整理
     *
     * @return
     */
    public void makeUp() {
        Map<Integer, BaseBagItem> tempGrids = new HashMap<>();
        // 全部尝试合并
        for (int i = START_GRID; i < capacity(); ++i) {
            for (int j = i + 1; j < capacity(); ++j) {
                try {
                    merge(i, j);
                } catch (ItemNotExistException | ItemNotSameTypeException | ItemOverStackException
                        | ItemZeroNumException ignored) {
                }
            }
        }
        // 开始排序
        List<BaseBagItem> tempList = new ArrayList<>(bag().values());
        Collections.sort(tempList);
        for (int i = START_GRID; i < capacity(); ++i) {
            BaseBagItem newItem = null;
            if (i < tempList.size()) {
                newItem = tempList.get(i);
                tempGrids.put(i, newItem);
            }
            BaseBagItem oldItem = bag().get(i);
            if (newItem != oldItem) {
                if (oldItem != null) {
                    onItemRemoved(i, oldItem);
                }
                if (newItem != null) {
                    onItemAdded(i, newItem);
                }
            }
        }
        bag().clear();
        bag().putAll(tempGrids);
    }

    /**
     * 拆分物品
     *
     * @param item
     * @param num
     * @throws ItemNotExistException
     * @throws ItemSliptNotEnoughNumException
     * @throws BagFullException
     * @throws BagGridOccupiedException
     * @throws ItemZeroNumException
     */
    private void split(BaseBagItem item, int num) throws ItemNotExistException, ItemSliptNotEnoughNumException,
            BagFullException, ItemZeroNumException {
        if (item == null) {
            throw new ItemNotExistException();
        }
        if (item.getNum() <= 1 || num > item.getNum()) {
            throw new ItemSliptNotEnoughNumException(item.getNum(), num);
        }
        int anEmptyGrid = getAnEmptyGrid();
        if (anEmptyGrid == -1) {
            throw new BagFullException();
        }
        int reduceNum = Math.max(1, num);
        item.setNum(item.getNum() - reduceNum);
        onItemChanged(item);
        BaseBagItem copy = item.copy();
        copy.setNum(reduceNum);
        addItem(anEmptyGrid, copy);
    }

    /**
     * 拆分物品
     *
     * @param gridId
     * @param num
     * @return
     * @throws BagFullException
     * @throws ItemSliptNotEnoughNumException
     * @throws ItemNotExistException
     * @throws BagGridOccupiedException
     * @throws ItemZeroNumException
     */
    public void split(int gridId, int num) throws ItemNotExistException, ItemSliptNotEnoughNumException,
            BagFullException, BagGridOccupiedException, ItemZeroNumException {
        BaseBagItem item = getItem(gridId);
        split(item, num);
        if (item.getNum() <= 0) {
            removeItem(gridId);
        }
    }

    /**
     * 合并物品
     *
     * @param me
     * @param another
     * @throws ItemNotSameTypeException
     * @throws ItemOverStackException
     * @throws ItemNotExistException
     * @throws ItemZeroNumException
     */
    private void merge(BaseBagItem me, BaseBagItem another)
            throws ItemNotSameTypeException, ItemOverStackException, ItemNotExistException, ItemZeroNumException {
        if (me == null || another == null) {
            throw new ItemNotExistException();
        }
        if (me.getNum() <= 0 || another.getNum() <= 0) {
            throw new ItemZeroNumException();
        }
        if (!me.isSameType(another)) {
            throw new ItemNotSameTypeException();
        }
        int meNum = me.getNum();
        int anotherNum = another.getNum();
        if (meNum >= me.getMaxStackNumber()) {
            throw new ItemOverStackException(me.getMaxStackNumber());
        }
        int meAfterNum = Math.min(me.getMaxStackNumber(), meNum + anotherNum);
        me.setNum(meAfterNum);
        onItemChanged(me);
        another.setNum(another.getNum() - (meAfterNum - meNum));
        onItemChanged(another);
    }

    /**
     * 合并物品
     *
     * @param gridOne
     * @param gridAnother
     * @throws ItemNotExistException
     * @throws ItemZeroNumException
     * @throws ItemOverStackException
     * @throws ItemNotSameTypeException
     */
    public void merge(int gridOne, int gridAnother)
            throws ItemNotExistException, ItemNotSameTypeException, ItemOverStackException, ItemZeroNumException {
        BaseBagItem me = getItem(gridOne);
        BaseBagItem another = getItem(gridAnother);
        merge(me, another);
        if (another.getNum() <= 0) {
            removeItem(gridAnother);
        }
    }

    /**
     * 获取一个空的格子
     *
     * @return -1为没有空格子
     */
    public int getAnEmptyGrid() {
        for (int i = START_GRID; i < capacity(); ++i) {
            if (bag().get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 内容大小
     *
     * @return
     */
    public int size() {
        return bag().size();
    }

    /**
     * 剩余空间
     *
     * @return
     */
    public int remain() {
        return Math.max(0, capacity() - size());
    }

    /**
     * 获取背包Map
     *
     * @return
     */
    protected abstract Map<Integer, BaseBagItem> bag();

    /**
     * 容量
     *
     * @return
     */
    public abstract int capacity();

    /**
     * 添加物品后
     *
     * @param gridId
     * @param item
     */
    protected abstract void onItemAdded(int gridId, BaseBagItem item);

    /**
     * 删除物品后
     *
     * @param gridId
     * @param item
     */
    protected abstract void onItemRemoved(int gridId, BaseBagItem item);

    /**
     * 物品信息产生变化
     *
     * @param item
     */
    public abstract void onItemChanged(BaseBagItem item);
}
