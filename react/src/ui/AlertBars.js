import { enqueueSnackbar } from 'notistack';

export const defaultSnackbar = (message) =>
    enqueueSnackbar(message, {
        variant: 'default'
    });

export const successSnackbar = (message) =>
    enqueueSnackbar(message, {
        variant: 'success',
        autoHideDuration: 2000
    });

export const warningSnackbar = (message) =>
    enqueueSnackbar(message, {
        variant: 'warning'
    });

export const infoSnackbar = (message) =>
    enqueueSnackbar(message, {
        variant: 'info',
        autoHideDuration: 1000
    });

export const errorSnackbar = (message) =>
    enqueueSnackbar(message, {
        variant: 'error',
        autoHideDuration: 4500
    });
