<template>
  <form>
    <label>Ustaw login</label>
    <input type="text" v-model="newuser.login">
    <label>Ustaw Hasło</label>
    <input type="password" v-model="newuser.password">
    <button type="submit">{{ labelOfTheButton }}</button>
  </form>
</template>

<script>
    export default {
        props: ["buttonLabel"],
        data() {
            return {
                newuser: {}
            };
        },
        methods: {
            register(user) {
                this.$emit("createUser", this.newuser);
            }
        },
        computed: {
            labelOfTheButton() {
                return this.buttonLabel || 'Zarejestruj się.';
            }
        }
    };
</script>
