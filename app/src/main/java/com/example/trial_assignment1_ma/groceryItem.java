package com.example.trial_assignment1_ma;

public class groceryItem {
        private Integer id;
        private String name;
        private Integer quantity;

        public groceryItem() {

        }

        public groceryItem(Integer ids, String names, Integer quantitys) {
            this.id = ids;
            this.name = names;
            this.quantity = quantitys;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Integer getid() {
            return id;
        }

        public void setid(Integer ids) {
            this.id = ids;
        }


    }



