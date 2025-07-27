import React from "react";
import { Container } from "@mui/system";
import Snackbars from "./ui/Snackbars";

const App = () => {

    return <Container sx={{
        height: '100vh'
    }}>
        <Snackbars />
    </Container>
}

export default App;