import React from "react";
import { Container } from "@mui/system";
import SSEEvents from "./ui/SSEEvents";
import { SnackbarProvider } from 'notistack';
import Slide from "@mui/material/Slide";

function SlideTransition(props) {
    return <Slide {...props} direction="left" />;
}

const App = () => {
    return <Container sx={{
        height: '100vh'
    }}>
        <SnackbarProvider
            TransitionComponent={SlideTransition}
            maxSnack={7}
            anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
            }}
            autoHideDuration={5000}
        >
            <SSEEvents/>
        </SnackbarProvider>
    </Container>
}

export default App;