/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jmeter.testelement;

/**
 * Identify if testElement can be skipped or not, Even attributes are reset in Test Element same
 * time logic  to be skipped should be implemented in testElement and using this interface will
 * identify testElement that are implement it.
 */
public interface Skippable {


    /**
     * Check if SKIPPED property is present and true ; defaults to false if empty.
     *
     * @return true if element is skipped
     */
    default boolean isSkipped() {
        return false;
    }

    /**
     * if element can be skipped. to display skipped field. it should be static value by type.
     */
    default boolean skippable() {
        return true;
    }
}
