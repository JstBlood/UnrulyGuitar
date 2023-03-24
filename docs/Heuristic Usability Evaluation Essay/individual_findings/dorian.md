# Group 22 heuristic evaluation form.

Please evaluate the system keeping in mind the following heuristics as you go:

1. **Has the system not communicated any errors or failures clearly to you (preferably immediately) ?
   If so please explain which, which things went wrong and what was unclear about the system’s interaction. Please also provide some way that this communication could be made clearer.**

The app shows the system status, all the changes are instantaneous

***

2. **Did you find the system to be inconsistent in any way shape or form?
   If so please point out the inconsistency and how it influences your usage of the system. How do you think the system could be made consistent?**

The list of previous boards is a list of ids that is not is the same theme as the rest of the app.

***

3. **Did the system steer you into error prone situations?
   If so please explain how, and enumerate the errors that arose as a result of this. Do you think this situation could be communicated better?**

When creating a list with an empty name, an error is thrown. we think it would be better to just not allow this situation to arise.

***

4. **Do you find the system efficient to use and flexible?
   If you do not, please provide examples of what is holding you back from achieving maximal efficiency whilst utilising the system.**

The app can be resized and its layout is adapted to the window's size, allowing for custom experience. There are no shortcuts available except the tab and shift-tab.

***

5. **Does the system provide enough documentation and/or help?
   If it does not, please showcase things which were hard to comprehend or ones that you did not understand. Explain how you would prefer them to be documented.**

There is no help nor documentation available, but the app is straightforward to use, so it is not really necessary in our opinion.

***

6. **Does the app have a clear way to exit / return from an unwanted interaction? (Ex: cancel button). Does it provide the user enough control and freedom? (Ex: undo / redo)
   If not, please indicate where the above mentioned are insufficient and what you are missing, as a user.**

The user has the freedom to do what he wants with the available buttons, and there are "go back" buttons represented as arrows on every scene. There is no undo/redo options though.

***

7. **Do the interactions in the system map well to the real world? Do operations feel intuitive to you?
   If not, please provide examples of where the system diverges from your understanding of the real world. How would you expect these things to work considering your understanding of the real world?**

    - When asked a server address, the user also needs to provide a port number. We think this should be avoided as a user might not know what a port number is.

    - In the select board scene, the user can see a list of previous board as a list of board ids. A user is not suppose to see this as it would mean having an understanding of the underlying database.

***

8. **Is the interface straightforward and easy to operate? Can you easily recognize information presented to you and navigate through it properly?
   If not, please mention how to help the user do that and how to eliminate the need to recall elements, actions, etc.**

No functionality is hidden, everything is easy to access and visible.

***

9. **Do you find the interface aesthetically pleasing? Would you use the system in marvel of the interfaces’ minimalistic and/or interesting design?
   If you do not please explain where our interface let you down design-wise. Do you think this could be improved? If you have a vision of a better looking interface do not hesitate to share it here.**

The design has a very specific theme with bright colors and a kinda blurry feeling, we are not really a fan of the color scheme.

***

10. **Are the error messages suggestive and do they offer an in-depth way of solving the problem / a path to diagnose the error and recover from it / fix it?
    If there are not, please explain precisely which errors you are referring to, and how they confused you. Do you think the error message could be different? Please provide a example of how you would construct this error message to be clear/er.**

When the empty list is created, a HTTP 400 error is thrown. The user has no idea what the meaning of it is and what caused it. It would be better to tell the user what caused this HTTP error instead.

***
