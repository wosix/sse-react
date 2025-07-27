import axios from "axios";

const appUrl = process.env.APP_URL;

const headers = {
    'Content-Type': 'application/json'
}

const ApplicationJsonConfig = {
    headers: headers
}

const Api = axios.create({
    baseURL: appUrl,
    responseType: "json"
});
