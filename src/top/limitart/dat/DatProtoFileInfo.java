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
package top.limitart.dat;


import java.util.ArrayList;
import java.util.List;

/**
 * 静态数据协议文件信息
 *
 * @author hank
 */
public class DatProtoFileInfo {
    private String _package;
    private String _name;
    private String _explain;
    private List<ColInfo> _cols = new ArrayList<>();

    public String get_package() {
        return _package;
    }

    public void set_package(String _package) {
        this._package = _package;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_explain() {
        return _explain;
    }

    public void set_explain(String _explain) {
        this._explain = _explain;
    }

    public List<ColInfo> get_cols() {
        return _cols;
    }

    public static class ColInfo {
        private String _name;
        private String _explain;
        private String _type;

        public String get_name() {
            return _name;
        }

        public void set_name(String _name) {
            this._name = _name;
        }

        public String get_explain() {
            return _explain;
        }

        public void set_explain(String _explain) {
            this._explain = _explain;
        }

        public String get_type() {
            return _type;
        }

        public void set_type(String _type) {
            this._type = _type;
        }
    }
}
