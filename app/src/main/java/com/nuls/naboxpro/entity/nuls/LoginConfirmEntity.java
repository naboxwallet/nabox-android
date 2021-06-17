package com.nuls.naboxpro.entity.nuls;

public class LoginConfirmEntity {


    /**
     * jsonrpc : 2.0
     * method : commitData
     * params : {"key":"eoihgfjsndv8we9r0sdf","value":{"address":"NULSd6HgdemcQDAaiEJWq9ESMhtUHXRsNJ4KM","terminal":"登录易 "}}
     * id : 1234
     */

    private String jsonrpc;
    private String method;
    private ParamsBean params;
    private int id;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class ParamsBean {
        /**
         * key : eoihgfjsndv8we9r0sdf
         * value : {"address":"NULSd6HgdemcQDAaiEJWq9ESMhtUHXRsNJ4KM","terminal":"登录易 "}
         */

        private String key;
        private ValueBean value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public ValueBean getValue() {
            return value;
        }

        public void setValue(ValueBean value) {
            this.value = value;
        }

        public static class ValueBean {
            /**
             * address : NULSd6HgdemcQDAaiEJWq9ESMhtUHXRsNJ4KM
             * terminal : 登录易
             */

            private String address;
            private String terminal;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getTerminal() {
                return terminal;
            }

            public void setTerminal(String terminal) {
                this.terminal = terminal;
            }
        }
    }
}
