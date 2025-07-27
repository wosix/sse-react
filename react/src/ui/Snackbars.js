import React, { useState } from "react";
import Button from '@mui/material/Button';
import Snackbar from '@mui/material/Snackbar';
import { Box } from "@mui/system";

const Snackbars = () => {
    const [open, setOpen] = useState(false);

    const handleOpen = () => {
        setOpen(true);
        console.log('hello');

    };

    const handleClose = () => {
        setOpen(false);
    };

    return <>
        <Box sx={{ width: 'min-content', margin: 'auto', paddingTop: '380px' }}>
            <Button color="warning" variant="contained"
                onClick={() => handleOpen()}
            >
                open
            </Button>
        </Box>

        <Snackbar
            anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
            }}
            open={open}
            onClick={() => handleClose()}
            message="I love snacks"
            key={"snackbar"}
        />
    </>
}

export default Snackbars;