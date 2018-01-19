/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 *
 */
public class DehydrateTestObject extends BaseTestObject {

    private List cars = new ArrayList();
    private List vans = new ArrayList();
    private List appleComputers;
    private List fruit = new ArrayList();
    private List iterateCars = new ArrayList();
    private List iterateMoreCars = new ArrayList();

    public List getFruit() {
        return fruit;
    }

    public void setFruit(List fruit) {
        this.fruit = fruit;
    }

    public List getCars() {
        return cars;
    }

    public void setCars(List cars) {
        this.cars = cars;
    }

    public List getAppleComputers() {
        return appleComputers;
    }

    public void setAppleComputers(List appleComputers) {
        this.appleComputers = appleComputers;
    }

    public List getVans() {
        return vans;
    }

    public void setVans(List vans) {
        this.vans = vans;
    }

    public Car buildCar() {
        Car car = new Car();
        car.setName("Build by buildCar");
        return car;
    }

    public List myIterateCars() {
        return iterateCars;
    }

    public void setIterateCars(List iterateCars) {
        this.iterateCars = iterateCars;
    }

    public void addCar(Car car) {
        this.iterateCars.add(car);
    }

    public List getIterateMoreCars() {
        return iterateMoreCars;
    }

    public void setIterateMoreCars(List iterateMoreCars) {
        this.iterateMoreCars = iterateMoreCars;
    }

    public void addMoreCar(Car car) {
        this.iterateMoreCars.add(car);
    }
}
