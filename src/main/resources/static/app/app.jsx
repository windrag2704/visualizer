import React from "react";
import ReactDom from "react-dom";

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            nodes: [],
            edges: []
        };
    }
    componentDidMount() {
        const apiUrl = 'api/get';
        fetch(apiUrl)
            .then((response) => response.json())
            .then((data) => this.setState({
                nodes: data.nodes,
                edges: data.edges
            }))
    }


    render() {
        console.log('Edges', this.state.edges);
        console.log('Nodes', this.state.nodes);
        return <p>Hello, World!</p>
    }
}
ReactDom.render(<App />, document.getElementById('react'));