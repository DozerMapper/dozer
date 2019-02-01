/*
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.vo.recursive;

public class ClassB implements Comparable<ClassB> {

    private String rue;
    private String ville;
    private ClassA parent;
    private int prime = 31;

    public ClassA getParent() {
        return this.parent;
    }

    public void setParent(final ClassA parent) {
        this.parent = parent;
    }

    public String getRue() {
        return this.rue;
    }

    public void setRue(final String rue) {
        this.rue = rue;
    }

    public String getVille() {
        return this.ville;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = prime * result + ((this.rue == null) ? 0 : this.rue.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        final ClassB other = (ClassB)obj;
        if (this.rue == null) {
            return other.rue == null;
        } else {
            return this.rue.equals(other.rue);
        }
    }

    public void setVille(final String ville) {
        this.ville = ville;
    }

    public int compareTo(ClassB o) {
        return 0;
    }
}
