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
package top.limitart.script;

import top.limitart.util.FileUtil;

import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 源码型JavaFileObject
 *
 * @author hank
 */
public class SourceCodeJavaFileObject extends SimpleJavaFileObject {

    private String code;

    public SourceCodeJavaFileObject(File sourceFile) throws IOException {
        super(sourceFile.toURI(), Kind.SOURCE);
        this.code = new String(FileUtil.readFile1(sourceFile), StandardCharsets.UTF_8);
    }

    public SourceCodeJavaFileObject(URI fileURI, InputStream fileInputStream)
            throws IOException {
        super(fileURI, Kind.SOURCE);
        this.code = new String(FileUtil.inputStream2ByteArray(fileInputStream), StandardCharsets.UTF_8);
    }

    public SourceCodeJavaFileObject(URI fileURI, byte[] fileContent) {
        super(fileURI, Kind.SOURCE);
        this.code = new String(fileContent, StandardCharsets.UTF_8);
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {
        return this.code;
    }
}
