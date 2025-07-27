import React, { useState } from "react";
import Snackbar from "@mui/material/Snackbar";
import Slide from "@mui/material/Slide";

function SlideTransition(props) {
    return <Slide {...props} direction="up" />;
}

const AlertBar = ({ message, props }) => {
    const [open, setOpen] = useState(true);

    const handleClose = () => {
        setOpen(false);
    }

    return <Snackbar {...props}
        anchorOrigin={{
            vertical: 'top',
            horizontal: 'right',
        }}
        open={open}
        onClick={() => handleClose()}
        message={message}
        autoHideDuration={5000}
        slots={{ transition: SlideTransition }}
    />

}


export default AlertBar;