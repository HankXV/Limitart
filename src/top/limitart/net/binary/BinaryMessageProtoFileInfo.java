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
package top.limitart.net.binary;

import java.util.ArrayList;
import java.util.List;

/**
 * 二进制消息协议文件信息
 *
 * @author hank
 */
public class BinaryMessageProtoFileInfo {
    private byte _id;
    private String _name;
    private String _package;
    private String _explain;
    private List<BinaryMessageMetaProtoInfo> _metas = new ArrayList<>();
    private List<BinaryMessageProtoInfo> _messages = new ArrayList<>();

    /**
     * @return the _id
     */
    public byte get_id() {
        return _id;
    }

    /**
     * @param _id the _id to set
     */
    public void set_id(byte _id) {
        this._id = _id;
    }

    /**
     * @return the _name
     */
    public String get_name() {
        return _name;
    }

    /**
     * @param _name the _name to set
     */
    public void set_name(String _name) {
        this._name = _name;
    }

    /**
     * @return the _package
     */
    public String get_package() {
        return _package;
    }

    /**
     * @param _package the _package to set
     */
    public void set_package(String _package) {
        this._package = _package;
    }

    /**
     * @return the _explain
     */
    public String get_explain() {
        return _explain;
    }

    /**
     * @param _explain the _explain to set
     */
    public void set_explain(String _explain) {
        this._explain = _explain;
    }

    /**
     * @return the _metas
     */
    public List<BinaryMessageMetaProtoInfo> get_metas() {
        return _metas;
    }

    /**
     * @param _metas the _metas to set
     */
    public void set_metas(List<BinaryMessageMetaProtoInfo> _metas) {
        this._metas = _metas;
    }

    /**
     * @return the _messages
     */
    public List<BinaryMessageProtoInfo> get_messages() {
        return _messages;
    }

    /**
     * @param _messages the _messages to set
     */
    public void set_messages(List<BinaryMessageProtoInfo> _messages) {
        this._messages = _messages;
    }

    public static class BinaryMessageMetaProtoInfo {
        private String _name;
        private String _explain;
        private List<BinaryMessageFieldProtoInfo> _fields = new ArrayList<>();

        /**
         * @return the _name
         */
        public String get_name() {
            return _name;
        }

        /**
         * @param _name the _name to set
         */
        public void set_name(String _name) {
            this._name = _name;
        }

        /**
         * @return the _explain
         */
        public String get_explain() {
            return _explain;
        }

        /**
         * @param _explain the _explain to set
         */
        public void set_explain(String _explain) {
            this._explain = _explain;
        }

        /**
         * @return the _fields
         */
        public List<BinaryMessageFieldProtoInfo> get_fields() {
            return _fields;
        }

        /**
         * @param _fields the _fields to set
         */
        public void set_fields(List<BinaryMessageFieldProtoInfo> _fields) {
            this._fields = _fields;
        }

    }

    public static class BinaryMessageProtoInfo extends BinaryMessageMetaProtoInfo {
        private byte _messageID;

        /**
         * @return the _messageID
         */
        public byte get_messageID() {
            return _messageID;
        }

        /**
         * @param _messageID the _messageID to set
         */
        public void set_messageID(byte _messageID) {
            this._messageID = _messageID;
        }

    }

    public static class BinaryMessageFieldProtoInfo {
        private int _isList;
        private String _type;
        private String _name;
        private String _explain;

        /**
         * 将类型转化为java对象类型
         */
        public void turnTypeToJavaBox() {
            switch (_type) {
                case "int":
                    this._type = "Integer";
                    break;
                case "byte":
                    this._type = "Byte";
                    break;
                case "short":
                    this._type = "Short";
                    break;
                case "long":
                    this._type = "Long";
                    break;
                case "float":
                    this._type = "Float";
                    break;
                case "double":
                    this._type = "Double";
                    break;
                case "boolean":
                    this._type = "Boolean";
                    break;
                case "char":
                    this._type = "Character";
                    break;
            }
        }

        /**
         * @return the _isList
         */
        public int get_isList() {
            return _isList;
        }

        /**
         * @param _isList the _isList to set
         */
        public void set_isList(int _isList) {
            this._isList = _isList;
        }

        /**
         * @return the _type
         */
        public String get_type() {
            return _type;
        }

        /**
         * @param _type the _type to set
         */
        public void set_type(String _type) {
            this._type = _type;
        }

        /**
         * @return the _name
         */
        public String get_name() {
            return _name;
        }

        /**
         * @param _name the _name to set
         */
        public void set_name(String _name) {
            this._name = _name;
        }

        /**
         * @return the _explain
         */
        public String get_explain() {
            return _explain;
        }

        /**
         * @param _explain the _explain to set
         */
        public void set_explain(String _explain) {
            this._explain = _explain;
        }

    }
}
